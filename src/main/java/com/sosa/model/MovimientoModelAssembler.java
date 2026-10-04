package com.sosa.model;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.sosa.controller.MovimientoController;
import com.sosa.model.dto.MovimientoDTO;

@Component
public class MovimientoModelAssembler extends RepresentationModelAssemblerSupport<MovimientoDTO, MovimientoDTO> {
	
	public MovimientoModelAssembler() {
		super(MovimientoController.class, MovimientoDTO.class);
	}

	@Override
	public MovimientoDTO toModel(MovimientoDTO entity) {
		ModelMapper mapper = new ModelMapper();
		return mapper.map(entity, MovimientoDTO.class);
	}

}
