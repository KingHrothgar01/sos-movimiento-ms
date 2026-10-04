package com.sosa.controller;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_005;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_012;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_013;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_014;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sosa.event.AbstractRestHandler;
import com.sosa.event.MovimientoServiceEvent;
import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP500Exception;
import com.sosa.model.MovimientoModelAssembler;
import com.sosa.model.dto.MovimientoDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.service.MovimientoService;

@RestController
@Validated
public class MovimientoController extends AbstractRestHandler {

	@Autowired
	private MovimientoService movimientoService;
	
	@Autowired
	private MovimientoModelAssembler movimientoModelAssembler;
	
	@Autowired
	private PagedResourcesAssembler<MovimientoDTO> pagedResourcesAssembler;

	@PostMapping(value = "/prestamos/v1/movimientos", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public MovimientoDTO createMovimiento(@Valid @RequestBody MovimientoDTO dto, HttpServletRequest request, HttpServletResponse response) {
		dto = (this.movimientoService.saveMovimiento(dto)).orElseThrow(
				() -> new HTTP500Exception(BUSINESS_MSG_ERR_M_012));
		eventPublisher.publishEvent(new MovimientoServiceEvent(this, "MovimientoCreado", dto));
		response.setHeader("Location", request.getRequestURL().append("/").append(dto.getIdMovimiento()).toString());
		return dto;
	}

	@GetMapping(value = "/prestamos/v1/movimientos", produces = "application/hal+json")
	@ResponseStatus(HttpStatus.OK)
	public PagedModel<MovimientoDTO> getAllMovimientos(
			@Valid @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUM) @Min(value = 0, message = "El numero de p\u00E1gina debe ser mayor o igual a cero.") Integer page,
			@Valid @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) @Min(value = 0, message = "El tama\u00F1o de p\u00E1gina debe ser mayor o igual a cero.") Integer size,
			@Valid @RequestParam(value = "property", defaultValue = DEFAULT_PAGE_PROPERTY) String prop,
			@Valid @RequestParam(value = "sortOrder", defaultValue = DEFAULT_PAGE_ORDER) String sortOrder,
			HttpServletRequest request, HttpServletResponse response) {
		Page<MovimientoDTO> movimientoPage = this.movimientoService.findAllMovimientos(
				new PagingDTO(page, size, Direction.fromString(sortOrder.toUpperCase()), prop));
		return pagedResourcesAssembler.toModel(movimientoPage, movimientoModelAssembler);
	}

	@GetMapping(value = "/prestamos/v1/movimientos/{id}", produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Optional<MovimientoDTO> getMovimiento(@PathVariable("id") Long id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Optional <MovimientoDTO> movimiento = this.movimientoService.findMovimiento(id);
		return checkResourceFound(movimiento);
	}

	@PutMapping(value = "/prestamos/v1/movimientos/{id}", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public MovimientoDTO updateMovimiento(@PathVariable("id") Long id, @Valid @RequestBody MovimientoDTO dto, HttpServletRequest request,
			HttpServletResponse response) {
		if (!id.equals(dto.getIdMovimiento()))
			throw new HTTP400Exception(BUSINESS_MSG_ERR_M_005);
		MovimientoDTO objetoActualizado = this.movimientoService.updateMovimiento(dto).orElseThrow(
				() -> new HTTP500Exception(BUSINESS_MSG_ERR_M_013));
		eventPublisher.publishEvent(new MovimientoServiceEvent(this, "MovimientoActualizado", objetoActualizado));
		return objetoActualizado;
	}

	@DeleteMapping(value = "/prestamos/v1/movimientos/{id}", produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public MovimientoDTO deleteMovimiento(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		MovimientoDTO objetoEliminado = this.movimientoService.deleteMovimiento(id).orElseThrow(
				() -> new HTTP500Exception(BUSINESS_MSG_ERR_M_014));
		eventPublisher.publishEvent(new MovimientoServiceEvent(this, "MovimientoEliminado", objetoEliminado));
		return objetoEliminado;
	}
}
