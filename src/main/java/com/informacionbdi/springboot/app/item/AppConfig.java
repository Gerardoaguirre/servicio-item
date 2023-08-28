package com.informacionbdi.springboot.app.item;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@Configuration
public class AppConfig {

	@Bean("clienteRest")
	@LoadBalanced //esto realiza el banaceo  de carga para las diferentes isntnacias de los microservicios
	// y en el ItemServiceImpl que es donde se tiene el consumo, se cambia la IP por el nombre del microservicio "servicio-productos"
	public RestTemplate registrarRestTemplate() { //es un cliente para trabajar con resttemplate htpp para aceder a recursos en otros microservicior
		return new RestTemplate();
	}
	
	//para cambiar el umbral del default configuration de circuitbreakeFactoryr*/
	/*que son 100 peticiones y umbrall del 50%*/
	/*esto se puede hacer en un archivo appication.yml. El cual tiene mayor prioridad por lo que estaria tomando esa configiracion y no esta*/
	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer(){
		return factory -> factory.configureDefault(id -> {
			return new Resilience4JConfigBuilder(id).circuitBreakerConfig(CircuitBreakerConfig.custom()
					.slidingWindowSize(10) //peticiones
					.failureRateThreshold(50)//porcetaje de eerores
					.waitDurationInOpenState(Duration.ofSeconds(10L))//la duracion que dirará el circuito abierto es del 10 seg
					.permittedNumberOfCallsInHalfOpenState(5)//limite de llamadas en el circuito medioa abierto una vez que se abre
					.slowCallRateThreshold(50)//umbral porcetnaje de llamadas lentas, si el umbral es mayor se llama el siguiente
					.slowCallDurationThreshold(Duration.ofSeconds(2L))//se registra la peticion si dura mas, y si pasa el umbral hace circuito
					.build())
//					.timeLimiterConfig(TimeLimiterConfig.ofDefaults())//se deja por default lo cual indica que tiene tolerancia de un segundo le peticion y si no, hace circuito. abajo se configura
					.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(3L)).build())
					.build();
		});
	}
}
