package com.sosa.util;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter 
@Setter
@ConfigurationProperties(prefix = "movimiento.service", ignoreUnknownFields = false)
public class ServiceProperties {

	@NotNull
	private String name = "Movimiento Spring Data JPA Service";
	
	@NotNull
	private String description = "Movimiento Spring Data JPA Database Features";
	
}