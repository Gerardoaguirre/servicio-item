package com.informacionbdi.springboot.app.item.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.informacionbdi.springboot.app.item.models.Item;
import com.informacionbdi.springboot.app.item.models.Producto;
import com.informacionbdi.springboot.app.item.models.service.ItemService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class ItemController {

	@Autowired
	@Qualifier("serviceFeign")//sirve para definir cual clase se instanciará. ya que se está inyectando una interface
	private ItemService itemService;
	
	
	@GetMapping("/listar")
	public List<Item> listar( @RequestParam(name="nombre") String nombre, @RequestHeader(name="token-request") String token ){ //estos parametros los agrega el gateway,ya que las peticiones desde el cliente pasan por el, y a su vez los redigire aqui
		System.out.println("nombre "+ nombre);
		System.out.println("token request "+ token);
		return itemService.findAll();
	}
	
	@HystrixCommand(fallbackMethod="metodoAlternativo") //esto hace la implementacion. EStamos simulacndo que falla el micro de producto en el controller para que se bvaya por otro camino (circuit breaker)
	@GetMapping("/ver/{id}/cantidad/{cantidad}")
	public Item detalle(@PathVariable Long id, @PathVariable Integer cantidad) {
		return itemService.findById(id, cantidad);
	}
	
	public Item metodoAlternativo(Long id, Integer cantidad) {//este se llama y puede llmaar a otro micro si es que no se encuentra en linea, metodo flujo.etc
		Item item =  new Item();
		Producto producto = new Producto();
		
		item.setCantidad(cantidad);
		producto.setId(id);
		producto.setNombre("Camar Sony reespuesta estatica para hace ruso de circuitbreaker");
		producto.setPrecio(500.00);
		item.setProducto(producto);
		
		return item;
	}
}
