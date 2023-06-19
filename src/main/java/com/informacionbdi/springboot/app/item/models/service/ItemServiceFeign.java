package com.informacionbdi.springboot.app.item.models.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.informacionbdi.springboot.app.item.clientes.ProductoClienteRest;
import com.informacionbdi.springboot.app.item.models.Item;
//este va configurado  con el SpringbootServicioItemApplication

//como tenemos dos resttemplate, debemo indicar cual será la que debe inyectarse
//ya que la interface se inyecta en el controller (ItemController.java), ya que alli se esta inyectando la interface
//En caso de no hacerlo con primary. en el controller debe uutlizarse el Qualifier e indicar el nombre del componente 
//que se inyectará
@Service("serviceFeign")
public class ItemServiceFeign implements ItemService {
	
	@Autowired
	private ProductoClienteRest clienteFeign;

	@Override
	public List<Item> findAll() {
		return clienteFeign.listar().stream().map(p -> new Item(p,1)).collect(Collectors.toList());
	}

	@Override
	public Item findById(Long id, Integer cantidad) {
		return new Item(clienteFeign.detalle(id),cantidad);
	}

}
