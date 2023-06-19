package com.informacionbdi.springboot.app.item.clientes;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.informacionbdi.springboot.app.item.models.Producto;

//con feign se define, se declara que es un cliente feign (desarrollada por netflix)
//para balanceo de carga
//otra forma de consumir los servicios
//tambien hace que la notacion pase a ser un componente manejado pro spring
//la alternativa de resttemplate
//@FeignClient(name = "servicio-productos",url="localhost:8001") //indicamos al microservicio que nos queremos conectar
//Para tener mas desacoplado la URL con la notacion (como la line ade arriba), se usa ribbon para balancear la carga en nuestras diferentes instancias
//y debemos configurarlo en el application.properties
@FeignClient(name = "servicio-productos")
public interface ProductoClienteRest {

	@GetMapping("/listar") //debe ser igual al servicio que nos queremos conectar para que se comunique
	public List<Producto> listar();
	
	@GetMapping("/ver/{id}")
	public Producto detalle(@PathVariable Long id);
}
