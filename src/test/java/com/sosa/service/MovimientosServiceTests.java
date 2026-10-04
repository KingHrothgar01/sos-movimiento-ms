package com.sosa.service;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_001;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_003;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_009;
import static com.sosa.util.Constants.USER_ALTA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP404Exception;
import com.sosa.model.Movimiento;
import com.sosa.model.dto.MovimientoDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.repository.MovimientoRepository;

@ExtendWith(MockitoExtension.class)
class MovimientosServiceTests {
	
	@Mock
	MovimientoRepository movimientoRepository;
	
	@InjectMocks
	MovimientoService movimientoService;
	
	private MovimientoDTO dto;
	
	private Movimiento model;
	
	private PagingDTO paging;

	@BeforeEach
	void setUp() throws Exception {
		dto = MovimientoDTO.builder()
				.idCatalogoMovimiento(1L)
				.idCatalogoOperacion(1L)
				.idPrestamo("SL-001-001")
				.monto(3000D)
				.fechaMovimiento(new Date())
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		ModelMapper mapper = new ModelMapper();
		model = mapper.map(dto, Movimiento.class);
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento.")
	void test_guardar_movimiento() {
		//given
		given(movimientoRepository.findById(dto.getIdMovimiento())).willReturn(Optional.empty());
		given(movimientoRepository.save(any(Movimiento.class))).willReturn(model);
		
		//when
		MovimientoDTO movimientoGuardado = (movimientoService.saveMovimiento(dto)).get();
		
		//then
		then(movimientoGuardado).isNotNull();
		then(movimientoGuardado.getActivo()).isTrue();
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento que ha sido guardado previamente - Escenario de error 1.")
	void test_guardar_movimiento_error_1() {
		//given
		given(movimientoRepository.findById(anyLong())).willReturn(Optional.of(model));
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			movimientoService.saveMovimiento(dto);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_M_001));
		verify(movimientoRepository, never()).save(any(Movimiento.class));
	}
	
	@Test
	@DisplayName("Test para obtener movimiento por id.")
	void test_obtener_movimiento() {
		//given
		given(movimientoRepository.findById(model.getIdMovimiento())).willReturn(Optional.ofNullable(model));
		
		//when
		Optional<MovimientoDTO> movimientos = movimientoService.findMovimiento(model.getIdMovimiento());
		
		//then
		assertThat(movimientos).isPresent();
		assertThat(movimientos.get().getMonto()).isEqualTo(3000D);
	}
	
	@Test
	@DisplayName("Test para obtener movimiento por id, movimiento no encontrado - Escenario de error 1.")
	void test_obtener_movimiento_error_1() {
		//given
		given(movimientoRepository.findById(anyLong())).willReturn(Optional.empty());
		
		//when
		HTTP404Exception thrown = assertThrows(HTTP404Exception.class, () -> {
			movimientoService.findMovimiento(1);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_M_003));
	}
	
	@Test
	@DisplayName("Test para eliminar un movimiento por id.")
	void test_eliminar_movimiento() {
		//given
		given(movimientoRepository.findById(anyLong())).willReturn(Optional.of(model));
		given(movimientoRepository.save(any(Movimiento.class))).willReturn(model);
		
		//when
		Optional<MovimientoDTO> movimientoBorrado = movimientoService.deleteMovimiento(1);
		
		//then
		verify(movimientoRepository, times(1)).save(any(Movimiento.class));
		then(movimientoBorrado).isNotNull();
		then(movimientoBorrado).isPresent();
		then(movimientoBorrado.get().getActivo()).isFalse();
	}
	
	@Test
	@DisplayName("Test para eliminar un movimiento por id, movimiento no encontrado - Escenario de error 1.")
	void test_eliminar_movimiento_error_1() {
		//given
		given(movimientoRepository.findById(anyLong())).willReturn(Optional.empty());
		
		//when
		HTTP404Exception thrown = assertThrows(HTTP404Exception.class, () -> {
			movimientoService.deleteMovimiento(1);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_M_009));
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento por id.")
	void test_actualiza_movimiento() {
		//given
		given(movimientoRepository.findById(anyLong())).willReturn(Optional.of(model));
		given(movimientoRepository.save(any(Movimiento.class))).willReturn(model);

		//when
		Optional<MovimientoDTO> movimiento = movimientoService.updateMovimiento(dto);
		
		//then
		assertThat(movimiento).isPresent();
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento por id, movimiento no encontrado - Escenario de error 1.")
	void test_actualiza_movimiento_error_1() {
		//given
		given(movimientoRepository.findById(anyLong())).willReturn(Optional.empty());
		
		//when
		assertThrows(HTTP404Exception.class, () -> {
			movimientoService.updateMovimiento(dto);
		});
		
		//then
		verify(movimientoRepository, never()).save(any(Movimiento.class));
	}
	
	@Test
	@DisplayName("Test para listar todos los movimientos existentes de forma ascendente.")
	void test_listar_movimientos_ascendente() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setOrder(Direction.ASC);
		paging.setProperty("idMovimiento");
		
		Movimiento one = Movimiento.builder()
				.monto(1000D)
				.fechaMovimiento(new Date())
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		Movimiento two = Movimiento.builder()
				.monto(2000D)
				.fechaMovimiento(new Date())
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		PageImpl<Movimiento> pagina = new PageImpl<Movimiento>(
				List.of(one, two),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()), 3);
		
		given(movimientoRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()))).willReturn(pagina);
		
		//when
		Page<MovimientoDTO> movimientos = movimientoService.findAllMovimientos(paging);
		
		//then
		assertThat(movimientos).isNotNull();
		assertThat(movimientos.getNumberOfElements()).isEqualTo(2);
		assertThat(movimientos.getTotalPages()).isEqualTo(2);
		assertThat(movimientos.getNumber()).isZero();
		assertThat(movimientos.getTotalElements()).isEqualTo(3);
		assertThat(movimientos.getSort()).isEqualTo(Sort.by("idMovimiento").ascending());
	}
	
	@Test
	@DisplayName("Test para listar todos los movimientos existentes de forma descendente.")
	void test_listar_movimientos_descendente() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setOrder(Direction.DESC);
		paging.setProperty("idMovimiento");
		
		Movimiento one = Movimiento.builder()
				.monto(1000D)
				.fechaMovimiento(new Date())
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		Movimiento two = Movimiento.builder()
				.monto(2000D)
				.fechaMovimiento(new Date())
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		PageImpl<Movimiento> pagina = new PageImpl<Movimiento>(
				List.of(one, two),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()), 3);
		
		given(movimientoRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()))).willReturn(pagina);
		
		//when
		Page<MovimientoDTO> movimientos = movimientoService.findAllMovimientos(paging);
		
		//then
		assertThat(movimientos).isNotNull();
		assertThat(movimientos.getNumberOfElements()).isEqualTo(2);
		assertThat(movimientos.getTotalPages()).isEqualTo(2);
		assertThat(movimientos.getNumber()).isZero();
		assertThat(movimientos.getTotalElements()).isEqualTo(3);
		assertThat(movimientos.getSort()).isEqualTo(Sort.by("idMovimiento").descending());
	}
	
	@Test
	@DisplayName("Test para listar todos los movimientos existentes de forma ascendente con numero de pagina mayor a 50.")
	void test_listar_movimientos_ascendente_large_page() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(52);
		paging.setOrder(Direction.ASC);
		paging.setProperty("idMovimiento");
		
		Movimiento one = Movimiento.builder()
				.monto(1000D)
				.fechaMovimiento(new Date())
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		Movimiento two = Movimiento.builder()
				.monto(2000D)
				.fechaMovimiento(new Date())
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		PageImpl<Movimiento> pagina = new PageImpl<Movimiento>(
				List.of(one, two),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()), 2);
		
		given(movimientoRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()))).willReturn(pagina);
		
		//when
		Page<MovimientoDTO> movimientos = movimientoService.findAllMovimientos(paging);
		
		//then
		assertThat(movimientos).isNotNull();
		assertThat(movimientos.getNumberOfElements()).isEqualTo(2);
		assertThat(movimientos.getTotalPages()).isEqualTo(1);
		assertThat(movimientos.getNumber()).isZero();
		assertThat(movimientos.getTotalElements()).isEqualTo(2);
		assertThat(movimientos.getSort()).isEqualTo(Sort.by("idMovimiento").ascending());
	}
	
	@Test
	@DisplayName("Test para listar todos los movimientos existentes, lista vacía - Escenario de error 1.")
	void testListaMovimientosVacia() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setOrder(Direction.DESC);
		paging.setProperty("idMovimiento");
		
		PageImpl<Movimiento> pagina = new PageImpl<Movimiento>(
				List.of(),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()), 0);
		
		given(movimientoRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()))).willReturn(pagina);
		
		//when
		Page<MovimientoDTO> movimientos = movimientoService.findAllMovimientos(paging);
		
		//then
		assertThat(movimientos).isNotNull();
		assertThat(movimientos).isEmpty();
		assertThat(movimientos.getNumberOfElements()).isZero();
		assertThat(movimientos.getTotalPages()).isZero();
		assertThat(movimientos.getNumber()).isZero();
		assertThat(movimientos.getTotalElements()).isZero();
		assertThat(movimientos.getSort()).isEqualTo(Sort.by("idMovimiento").descending());
	}
}
