package com.informacionbdi.springboot.app.item.models.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.informacionbdi.springboot.app.item.models.Item;
import com.informacionbdi.springboot.app.item.models.Producto;


//este va cnofigurado con el AppConfig
@Service("serviceRestTemplate")
public class ItemServiceImpl implements ItemService {

	@Autowired
	private RestTemplate clienteRest;
	
	@Override
	public List<Item> findAll() {
		//List<Producto> productos=Arrays.asList(clienteRest.getForObject("http://localhost:8001/listar",Producto[].class));
		List<Producto> productos=Arrays.asList(clienteRest.getForObject("http://servicio-productos/listar",Producto[].class));//para el balanceo de carga que se tiene configurado en el "AppConfig"
		//el endpoint regresa una lista de productos. Tenemos que convertirla en un arreglo de Items
		return productos.stream().map(p -> new Item(p,1)).collect(Collectors.toList());
	}

	@Override
	public Item findById(Long id, Integer cantidad) {
		Map<String, String> pathVariables = new HashMap<String, String >();
		pathVariables.put("id",id.toString());
		//Producto producto = clienteRest.getForObject("http://localhost:8001/ver/{id}", Producto.class,pathVariables);
		Producto producto = clienteRest.getForObject("http://servicio-productos/ver/{id}", Producto.class,pathVariables);
		return new Item(producto, cantidad);
	}

}
