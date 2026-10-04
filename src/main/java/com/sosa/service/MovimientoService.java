package com.sosa.service;

import static com.sosa.util.Constants.APPLICATION_MESSAGE_002;
import static com.sosa.util.Constants.APPLICATION_PARAMETER_LARGE_PAGE;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_001;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_003;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_006;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_009;
import static com.sosa.util.Constants.CUSTOM_METRICS_LARGE_PAYLOAD;
import static com.sosa.util.Constants.REGISTRO_ACTIVO;
import static com.sosa.util.Constants.REGISTRO_INACTIVO;
import static com.sosa.util.Constants.USER_ALTA;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP404Exception;
import com.sosa.model.Movimiento;
import com.sosa.model.dto.MovimientoDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.repository.MovimientoRepository;

import io.micrometer.core.instrument.Metrics;

@Service
public class MovimientoService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MovimientoService.class);
	
	@Autowired
	private MovimientoRepository movimientoRepository;

	public Optional<MovimientoDTO> saveMovimiento(MovimientoDTO movimiento) {
		Optional<Movimiento> saved = movimientoRepository.findById(movimiento.getIdMovimiento());
	
		if (saved.isPresent())
			throw new HTTP400Exception(BUSINESS_MSG_ERR_M_001);
	
		ModelMapper mapper = new ModelMapper();
		Movimiento model = mapper.map(movimiento, Movimiento.class);
		model.setFechaRegistro(new Date());
		model.setUsuarioRegistra(USER_ALTA);
		model.setActivo(REGISTRO_ACTIVO);
		return Optional.ofNullable(mapper.map(movimientoRepository.save(model), MovimientoDTO.class));
	}

	public Optional<MovimientoDTO> findMovimiento(long id) {
		Optional<Movimiento> model = movimientoRepository.findById(id);
		
		if (model.isPresent()) {
			ModelMapper mapper = new ModelMapper();
			return Optional.ofNullable(mapper.map(model.get(), MovimientoDTO.class));
		}else
			throw new HTTP404Exception(BUSINESS_MSG_ERR_M_003);
	}

	public Optional<MovimientoDTO> updateMovimiento(MovimientoDTO movimiento) {
		Optional<Movimiento> saved = movimientoRepository.findById(movimiento.getIdMovimiento());
		
		if (saved.isEmpty())
			throw new HTTP404Exception(BUSINESS_MSG_ERR_M_006);
		
		ModelMapper mapper = new ModelMapper();
		Movimiento model = mapper.map(movimiento, Movimiento.class);
		model.setFechaRegistro(saved.get().getFechaRegistro());
		model.setUsuarioRegistra(saved.get().getUsuarioRegistra());
		model.setFechaActualizacion(new Date());
		model.setUsuarioActualiza(USER_ALTA);
		model.setActivo(saved.get().getActivo());
		return Optional.of(mapper.map(movimientoRepository.save(model), MovimientoDTO.class));
	}

	public Optional<MovimientoDTO> deleteMovimiento(long idMovimiento) {
		Optional<Movimiento> saved = movimientoRepository.findById(idMovimiento);
		
		if (saved.isEmpty())
			throw new HTTP404Exception(BUSINESS_MSG_ERR_M_009);
		
		ModelMapper mapper = new ModelMapper();
		saved.get().setActivo(REGISTRO_INACTIVO);
		saved.get().setFechaActualizacion(new Date());
		saved.get().setUsuarioActualiza(USER_ALTA);
		Movimiento model = mapper.map(saved, Movimiento.class);
		return Optional.of(mapper.map(movimientoRepository.save(model), MovimientoDTO.class));
	}

	public Page<MovimientoDTO> findAllMovimientos(PagingDTO dto) {		
		Page<Movimiento> pageOfTransactions = null;
		
		if (dto.getOrder().isAscending())
			pageOfTransactions = movimientoRepository.findAll(PageRequest.of(
					dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).ascending()));
		else
			pageOfTransactions = movimientoRepository.findAll(PageRequest.of(
					dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).descending()));
		
		if (dto.getSize() > APPLICATION_PARAMETER_LARGE_PAGE) {
			LOGGER.info(APPLICATION_MESSAGE_002);
			Metrics.counter(CUSTOM_METRICS_LARGE_PAYLOAD).increment();
		}
		return transform(pageOfTransactions);
	}
	
	private Page<MovimientoDTO> transform (Page<Movimiento> movimiento) {
		return new PageImpl<MovimientoDTO>(
				getListMovimientos(movimiento), movimiento.getPageable(), movimiento.getTotalElements());
	}
	
	private List<MovimientoDTO> getListMovimientos(Page<Movimiento> page){
		ModelMapper mapper = new ModelMapper();
		return page.getContent().stream().map(
				n -> mapper.map(n, MovimientoDTO.class)).collect(Collectors.toList());
	}
}
