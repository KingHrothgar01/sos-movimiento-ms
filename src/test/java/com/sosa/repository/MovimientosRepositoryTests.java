package com.sosa.repository;

import static com.sosa.util.Constants.USER_ALTA;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.sosa.model.CatalogoMovimiento;
import com.sosa.model.CatalogoOperacion;
import com.sosa.model.Cliente;
import com.sosa.model.Movimiento;
import com.sosa.model.Prestamo;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
class MovimientosRepositoryTests {
	
	@Autowired
	MovimientoRepository movimientoRepository;
	
	@Autowired
	ClienteRepository clienteRepository;
	
	@Autowired
	CatalogoMovimientoRepository catalogoMovimientoRepository;
	
	@Autowired
	CatalogoOperacionRepository catalogoOperacionRepository;
	
	@Autowired
	PrestamoRepository prestamoRepository;
	
	private Cliente clienteCero;
	private CatalogoOperacion catOperacionCero;
	private CatalogoMovimiento catMovimientoCero;
	private Prestamo prestamoCero;
	private Movimiento movimientoCero;
	
	@BeforeEach
	void setUp() throws Exception {
		clienteCero = Cliente.builder()
				.idCliente("0000000001")
				.nombre("Jose Juan")
				.apellidoPaterno("Sosa")
				.apellidoMaterno("Romero")
				.rfc("SORJ8204085P0")
				.curp("SORJ820408HDF")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		clienteRepository.save(clienteCero); 
		
		catMovimientoCero = CatalogoMovimiento.builder()
				.descripcion("Cargo")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		catalogoMovimientoRepository.save(catMovimientoCero);
		
		catOperacionCero = CatalogoOperacion.builder()
				.descripcion("Comision")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		catalogoOperacionRepository.save(catOperacionCero);
		
		prestamoCero = Prestamo.builder()
				.idPrestamo("SL-001-001")
				.monto(120000D)
				.saldoInsoluto(100000D)
				.cliente(clienteCero)
				.diaCorte(3)
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		prestamoRepository.save(prestamoCero);
		
		movimientoCero = Movimiento.builder()
				.monto(3000D)
				.catalogoOperacion(catOperacionCero)
				.catalogoMovimiento(catMovimientoCero)
				.prestamo(prestamoCero)
				.fechaMovimiento(new Date())
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("impersonator")
				.usuarioActualiza(null)
				.activo(true)
				.build();
				
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento.")
	void testGuardarMovimiento() {
		// given
		// movimientoCero
		
		// when
		Movimiento movimientoGuardado = movimientoRepository.save(movimientoCero);
		
		// then
		assertThat(movimientoGuardado).isNotNull();
		assertThat(movimientoGuardado.getMonto()).isEqualTo(3000D);
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento.")
	void test_actualizar_movimiento() {
		Date fechaActualizacion = new Date();
		
		//given
		movimientoRepository.save(movimientoCero);
		
		//when
		Movimiento movimientoGuardado = movimientoRepository.findById(movimientoCero.getIdMovimiento()).orElse(null);
		movimientoGuardado.setMonto(5000D);
		movimientoGuardado.setFechaActualizacion(fechaActualizacion);
		movimientoGuardado.setUsuarioActualiza(USER_ALTA);
		Movimiento movimientoActualizado = movimientoRepository.save(movimientoGuardado);
		
		//then
		assertThat(movimientoActualizado.getMonto()).isEqualTo(5000D);
		assertThat(movimientoActualizado.getFechaActualizacion()).isEqualTo(fechaActualizacion);
		assertThat(movimientoActualizado.getUsuarioActualiza()).isEqualTo(USER_ALTA);
	}
	
	@Test
	@DisplayName("Test para eliminar un movimiento.")
	void test_eliminar_movimiento() {
		Date fechaActualizacion = new Date();
		//given
		movimientoRepository.save(movimientoCero);
		
		//when
		Movimiento movimientoGuardado = movimientoRepository.findById(movimientoCero.getIdMovimiento()).orElse(null);
		movimientoGuardado.setActivo(false);
		movimientoGuardado.setFechaActualizacion(fechaActualizacion);
		movimientoGuardado.setUsuarioActualiza("impersonator");
		Movimiento movimientoActualizado = movimientoRepository.save(movimientoGuardado);
		
		//then
		assertThat(movimientoActualizado).isNotNull();
		assertThat(movimientoActualizado.getActivo()).isFalse();
		assertThat(movimientoActualizado.getFechaActualizacion()).isEqualTo(fechaActualizacion);
		assertThat(movimientoActualizado.getUsuarioActualiza()).isEqualTo("impersonator");
	}
	
	@Test
	@DisplayName("Test para listar los movimientos.")
	void test_listar_movimientos() {
		//given
		Movimiento movimientoUno = Movimiento.builder()
				.monto(10000D)
				.catalogoOperacion(catOperacionCero)
				.catalogoMovimiento(catMovimientoCero)
				.prestamo(prestamoCero)
				.fechaMovimiento(new Date())
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		Movimiento movimientoDos = Movimiento.builder()
				.monto(10000D)
				.catalogoOperacion(catOperacionCero)
				.catalogoMovimiento(catMovimientoCero)
				.prestamo(prestamoCero)
				.fechaMovimiento(new Date())
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		movimientoRepository.save(movimientoCero);
		movimientoRepository.save(movimientoUno);
		movimientoRepository.save(movimientoDos);
		
		//when
		Page<Movimiento> listaMovimientos = movimientoRepository.findAll(PageRequest.of(0, 2, Sort.by("idMovimiento").ascending()));
		//then
		assertThat(listaMovimientos).isNotNull();
		assertThat(listaMovimientos.getSize()).isEqualTo(2);
		assertThat(listaMovimientos.getNumber()).isZero();
		assertThat(listaMovimientos.getNumberOfElements()).isEqualTo(2);
		assertThat(listaMovimientos.getSort().toString()).hasToString("idMovimiento: ASC");
		assertThat(listaMovimientos.getTotalElements()).isEqualTo(3);
		assertThat(listaMovimientos.getTotalPages()).isEqualTo(2);
	}
	
	@Test
	@DisplayName("Test para obtener un movimiento.")
	void test_obtener_movimiento_por_id() {
		//given
		movimientoRepository.save(movimientoCero);
		//when
		Movimiento movimientoGuardado = movimientoRepository.findById(movimientoCero.getIdMovimiento()).orElse(null);
		//then
		assertThat(movimientoGuardado).isNotNull();
		assertThat(movimientoGuardado.getIdMovimiento()).isGreaterThan(0);
		assertThat(movimientoGuardado.getActivo()).isTrue();
	}

}
