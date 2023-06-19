package com.informacionbdi.springboot.app.item;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

	@Bean("clienteRest")
	@LoadBalanced //esto realiza el banaceo  de carga para las diferentes isntnacias de los microservicios
	// y en el ItemServiceImpl que es donde se tiene el consumo, se cambia la IP por el nombre del microservicio "servicio-productos"
	public RestTemplate registrarRestTemplate() { //es un cliente para trabajar con resttemplate htpp para aceder a recursos en otros microservicior
		return new RestTemplate();
	}
}
