package com.sosa.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.sosa.model.CatalogoMovimiento;
import com.sosa.model.CatalogoOperacion;
import com.sosa.model.Movimiento;
import com.sosa.model.Prestamo;
import com.sosa.model.dto.MovimientoDTO;

class CustomConverterTests {
	
	private static ModelMapper mapper;

	@BeforeAll
	static void setUp() throws Exception {
		mapper = new ModelMapper();
		mapper.addMappings(CustomConverter.movimientoMap);
		mapper.addMappings(CustomConverter.movimientoDTOMap);
	}

	@Test
	void mapper_custom_copy_dto_2_entity() {
		MovimientoDTO dto = MovimientoDTO.builder()
				.idCatalogoMovimiento(1L)
				.idCatalogoOperacion(2L)
				.idPrestamo("3L")
				.fechaMovimiento(new Date())
				.monto(3000D)
				.usuarioActualiza("s1209465")
				.usuarioRegistra("s3498657")
				.fechaActualizacion(new Date())
				.fechaRegistro(new Date())
				.idMovimiento(5L)
				.build();
				
		Movimiento entity = mapper.map(dto, Movimiento.class);
		
		assertNotNull(entity);
		assertNotNull(entity.getCatalogoMovimiento());
		assertEquals(1L, entity.getCatalogoMovimiento().getIdCatalogoMovimiento());
		
		assertNotNull(entity.getCatalogoOperacion());
		assertEquals(2L, entity.getCatalogoOperacion().getIdCatalogoOperacion());
		
		assertNotNull(entity.getPrestamo());
		assertEquals("3L", entity.getPrestamo().getIdPrestamo());
	}
	
	@Test
	void mapper_custom_copy_entity_2_dto() {
		CatalogoMovimiento catMov = new CatalogoMovimiento();
		catMov.setIdCatalogoMovimiento(1L);
		
		CatalogoOperacion catOper = new CatalogoOperacion();
		catOper.setIdCatalogoOperacion(2L);
		
		Prestamo p = new Prestamo();
		p.setIdPrestamo("3L");
		
		Movimiento entity = Movimiento.builder()
				.catalogoMovimiento(catMov)
				.catalogoOperacion(catOper)
				.prestamo(p)
				.fechaMovimiento(new Date())
				.monto(3000D)
				.usuarioActualiza("s1209465")
				.usuarioRegistra("s3498657")
				.fechaActualizacion(new Date())
				.fechaRegistro(new Date())
				.idMovimiento(5L)
				.build();
				
		MovimientoDTO dto = mapper.map(entity, MovimientoDTO.class);
		
		assertNotNull(dto);
		//assertNotNull(dto.getIdCatMovimiento());
		//assertEquals(1L, dto.getIdCatMovimiento());
		
		//assertNotNull(dto.getIdCatOperacion());
		//assertEquals(2L, dto.getIdCatOperacion());
		
		assertNotNull(dto.getIdPrestamo());
		assertEquals("3L", dto.getIdPrestamo());
	}

}
