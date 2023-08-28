package com.informacionbdi.springboot.app.item.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.informacionbdi.springboot.app.item.models.Item;
import com.informacionbdi.springboot.app.item.models.Producto;
import com.informacionbdi.springboot.app.item.models.service.ItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand; //obsoleto, se usa recilience

@RestController
public class ItemController {
	
	
	private final Logger logger = LoggerFactory.getLogger(ItemController.class);
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	@Qualifier("serviceFeign")//sirve para definir cual clase se instanciará. ya que se está inyectando una interface
	private ItemService itemService;
	
	
	@GetMapping("/listar")
	public List<Item> listar( @RequestParam(name="nombre") String nombre, @RequestHeader(name="token-request") String token ){ //estos parametros los agrega el gateway,ya que las peticiones desde el cliente pasan por el, y a su vez los redigire aqui
		System.out.println("nombre "+ nombre);
		System.out.println("token request "+ token);
		return itemService.findAll();
	}
	
	//@HystrixCommand(fallbackMethod="metodoAlternativo") //esto hace la implementacion. EStamos simulacndo que falla el micro de producto en el controller para que se bvaya por otro camino (circuit breaker), es viejo (se usa resilience (lamdas))
	@GetMapping("/ver/{id}/cantidad/{cantidad}")
	public Item detalle(@PathVariable Long id, @PathVariable Integer cantidad) {
//		return itemService.findById(id, cantidad);
		return cbFactory.create("items")//se puede nombrar como sea. es nuestro identificador
				.run(()-> itemService.findById(id, cantidad), e  -> metodoAlternativo(id, cantidad,e));//tiene un umbral por fedecto del 50% (de 100 erquesr)  de fallas para que se habra el circuito y dura un minuto. todo esto para que reediriga la peticion y ya no el micro
															/*si noo se define al camino alternativo, devolverá el error de micro que consume items*/
															/*en el AppCondig tenemos configurado el umbral para cambiar el default configuration*/
	}
	

	//nuestro identificador"items" es el que tiene nuestro "application.yml"
	@CircuitBreaker(name="items", fallbackMethod= "metodoAlternativo")//para hacer uso de notacion, se debe realizar el application.yml. No funciona como circuitbreakerfactory
	@GetMapping("/ver2/{id}/cantidad/{cantidad}")
	public Item detalle2(@PathVariable Long id, @PathVariable Integer cantidad) {
		return itemService.findById(id, cantidad);
	}
	
	@CircuitBreaker(name="items",fallbackMethod= "metodoAlternativo2")//para combinar ambos o solo manejar time o circuitbreaker
	@TimeLimiter(name="items")//envuelve una llamda asincrona para calcular el tiempo de espera y para que funcione el mentodo alternativo se debe declarar en @CircuitBreaker
	@GetMapping("/ver3/{id}/cantidad/{cantidad}")
	public CompletableFuture<Item>  detalle3(@PathVariable Long id, @PathVariable Integer cantidad) {
		return CompletableFuture.supplyAsync(()->itemService.findById(id, cantidad));
	}
	
	public Item metodoAlternativo(Long id, Integer cantidad, Throwable e) {//este se llama y puede llmaar a otro micro si es que no se encuentra en linea, metodo flujo.etc
		logger.info(e.getMessage());
		Item item =  new Item();
		Producto producto = new Producto();
		
		item.setCantidad(cantidad);
		producto.setId(id);
		producto.setNombre("Camar Sony | reespuesta estatica para hace ruso de circuitbreaker");
		producto.setPrecio(500.00);
		item.setProducto(producto);
		
		return item;
	}
	
	public CompletableFuture<Item> metodoAlternativo2(Long id, Integer cantidad, Throwable e) {//definido para  el metodo detalle3()
		logger.info(e.getMessage());
		Item item =  new Item();
		Producto producto = new Producto();
		
		item.setCantidad(cantidad);
		producto.setId(id);
		producto.setNombre("Camar Sony | reespuesta estatica para hace ruso de circuitbreaker");
		producto.setPrecio(500.00);
		item.setProducto(producto);
		
		return CompletableFuture.supplyAsync(()->item);
	}
	
}
