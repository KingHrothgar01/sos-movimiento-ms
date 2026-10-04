package com.sosa.model.dto;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO extends RepresentationModel<MovimientoDTO> implements Serializable{
	private static final long serialVersionUID = -682101270979005886L;

	@JsonProperty("id-movimiento")
	@JsonSetter("id-movimiento")
	private long idMovimiento;
	
	@NotNull(message = "El tipo de operación es requerido.")
	@Positive(message = "El identificador de operaci\u00F3n debe ser mayor a 0.")
	@JsonProperty("catalogo-operacion")
	@JsonSetter("catalogo-operacion")
	private long idCatalogoOperacion;
	
	@NotNull(message = "El monto es requerido.")
	private Double monto;
	
	@NotNull(message = "El tipo de movimiento es requerido.")
	@Positive(message = "El identificador de movimiento debe ser mayor a 0.")
	@JsonProperty("catalogo-movimiento")
	@JsonSetter("catalogo-movimiento")
	private long idCatalogoMovimiento;
	
	@NotBlank(message = "El numero de prestamo es requerido.")
	@JsonProperty("prestamo")
	@JsonSetter("prestamo")
	private String idPrestamo;

	@NotNull(message = "La fecha del movimiento es requerida.")
	@JsonProperty("fecha-movimiento")
	@JsonSetter("fecha-movimiento")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date fechaMovimiento;
	
	@JsonProperty(value = "fecha-registro", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("fecha-registro")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date fechaRegistro;

	@JsonProperty(value = "fecha-actualizacion", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("fecha-actualizacion")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date fechaActualizacion;

	@JsonProperty(value = "usuario-registra", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("usuario-registra")
	private String usuarioRegistra;

	@JsonProperty(value = "usuario-actualiza", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("usuario-actualiza")
	private String usuarioActualiza;
	
	@JsonProperty(value = "activo", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("activo")
	private Boolean activo;
}
