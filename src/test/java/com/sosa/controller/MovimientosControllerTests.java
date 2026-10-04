package com.sosa.controller;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_002;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_004;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_005;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_008;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_010;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_012;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_013;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_M_014;
import static com.sosa.util.Constants.HTTP_MSG_400;
import static com.sosa.util.Constants.HTTP_MSG_404;
import static com.sosa.util.Constants.HTTP_MSG_500;
import static com.sosa.util.Constants.USER_ALTA;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sosa.event.GlobalExceptionHandler;
import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP404Exception;
import com.sosa.exception.HTTP500Exception;
import com.sosa.model.MovimientoModelAssembler;
import com.sosa.model.dto.MovimientoDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.service.MovimientoService;

@WebMvcTest(controllers = MovimientosControllerTests.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {MovimientoController.class, GlobalExceptionHandler.class, MovimientoModelAssembler.class})
public class MovimientosControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private MovimientoService movimientoService;
	
	@MockBean
	private PagedResourcesAssembler<MovimientoDTO> pagedResourcesAssembler;
	
	MovimientoDTO dto;
	
	@BeforeEach
	void setup() {
		LocalDateTime localDateTime = LocalDateTime.of(2024, Month.JANUARY, 1, 10, 10, 30);
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		Date date = Date.from(instant);
		
		dto = MovimientoDTO.builder()
				.idMovimiento(1)
				.idCatalogoOperacion(1)
				.monto(50000D)
				.idCatalogoMovimiento(1)
				.idPrestamo("0000000001")
				.fechaMovimiento(date)
				.fechaRegistro(date)
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
	}

	@Test
	@DisplayName("Test para guardar un movimiento.")
	void test_crear_movimiento() throws Exception{
		// given
		given(movimientoService.saveMovimiento(any(MovimientoDTO.class)))
			.willAnswer((invocation) -> Optional.ofNullable(invocation.getArgument(0)));
		
		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/movimientos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print()).andExpect(status().isCreated())
			.andExpect(content().contentType("application/json"))
			.andExpect(header().exists("Location"))
			.andExpect(jsonPath("$.id-movimiento", is(1)));
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento, DTO nulo - Escenario de error 1.")
	void test_crear_movimiento_error_1() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(post("/prestamos/v1/movimientos")
			   .contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException))
			    .andExpect(result -> assertEquals("Required request body is missing: public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.createMovimiento(com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento, Catalogo Operación no permitido - Escenario de error 2.")
	void test_crear_movimiento_error_2() throws Exception {
		// given
		dto.setIdCatalogoOperacion(-1);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/movimientos")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.createMovimiento(com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'idCatalogoOperacion': rejected value [-1]; codes [Positive.movimientoDTO.idCatalogoOperacion,Positive.idCatalogoOperacion,Positive.long,Positive]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.idCatalogoOperacion,idCatalogoOperacion]; arguments []; default message [idCatalogoOperacion]]; default message [El identificador de operación debe ser mayor a 0.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento, Catalogo Movimiento no permitido - Escenario de error 3.")
	void test_crear_movimiento_error_3() throws Exception {
		// given
		dto.setIdCatalogoMovimiento(0);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/movimientos")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.createMovimiento(com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'idCatalogoMovimiento': rejected value [0]; codes [Positive.movimientoDTO.idCatalogoMovimiento,Positive.idCatalogoMovimiento,Positive.long,Positive]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.idCatalogoMovimiento,idCatalogoMovimiento]; arguments []; default message [idCatalogoMovimiento]]; default message [El identificador de movimiento debe ser mayor a 0.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento, Monto nulo - Escenario de error 4.")
	void test_crear_movimiento_error_4() throws Exception {
		// given
		dto.setMonto(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/movimientos")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.createMovimiento(com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'monto': rejected value [null]; codes [NotNull.movimientoDTO.monto,NotNull.monto,NotNull.java.lang.Double,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.monto,monto]; arguments []; default message [monto]]; default message [El monto es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento, Monto cero - Escenario de error 5.")
	void test_crear_movimiento_error_5() throws Exception {
		// given
		dto.setMonto(0D);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/movimientos")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.createMovimiento(com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'monto': rejected value [0.0]; codes [Positive.movimientoDTO.monto,Positive.monto,Positive.java.lang.Double,Positive]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.monto,monto]; arguments []; default message [monto]]; default message [El monto debe ser mayor a 0.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento, Prestamo nulo - Escenario de error 6.")
	void test_crear_movimiento_error_6() throws Exception {
		// given
		dto.setIdPrestamo(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/movimientos")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.createMovimiento(com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'idPrestamo': rejected value [null]; codes [NotBlank.movimientoDTO.idPrestamo,NotBlank.idPrestamo,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.idPrestamo,idPrestamo]; arguments []; default message [idPrestamo]]; default message [El numero de prestamo es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento, Prestamo vac\u00EDo - Escenario de error 7.")
	void test_crear_movimiento_error_7() throws Exception {
		// given
		dto.setIdPrestamo(" ");

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/movimientos")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.createMovimiento(com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'idPrestamo': rejected value [ ]; codes [NotBlank.movimientoDTO.idPrestamo,NotBlank.idPrestamo,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.idPrestamo,idPrestamo]; arguments []; default message [idPrestamo]]; default message [El numero de prestamo es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento, Fecha Movimiento nula - Escenario de error 8.")
	void test_crear_movimiento_error_8() throws Exception {
		// given
		dto.setFechaMovimiento(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/movimientos")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.createMovimiento(com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'fechaMovimiento': rejected value [null]; codes [NotNull.movimientoDTO.fechaMovimiento,NotNull.fechaMovimiento,NotNull.java.util.Date,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.fechaMovimiento,fechaMovimiento]; arguments []; default message [fechaMovimiento]]; default message [La fecha del movimiento es requerida.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento, Error en capa de servicio - Escenario de error 9.")
	void test_crear_movimiento_error_9() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		
		// given
		given(movimientoService.saveMovimiento(any(MovimientoDTO.class))).willReturn(Optional.empty());

		// when
		ResultActions response = mockMvc.perform(post("/prestamos/v1/movimientos")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().is5xxServerError())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP500Exception))
			    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_M_012, result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_012)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepci\u00F3n cu\u00E1ndo el movimiento ya existe - Escenario de error 10.")
	void test_crear_movimiento_error_10() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(movimientoService.saveMovimiento(any(MovimientoDTO.class))).thenThrow(HTTP400Exception.class);
		
		ResultActions response = mockMvc.perform(post("/prestamos/v1/movimientos")
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepci\u00F3n cu\u00E1ndo la DB es inalcanzable - Escenario de error 11.")
	void test_crear_movimiento_error_11() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(movimientoService.saveMovimiento(any(MovimientoDTO.class))).thenThrow(CannotCreateTransactionException.class);
		
		ResultActions response = mockMvc.perform(post("/prestamos/v1/movimientos")
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CannotCreateTransactionException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento.")
	void test_actualizar_movimiento() throws Exception{
		// given
		given(movimientoService.updateMovimiento(any(MovimientoDTO.class)))
			.willAnswer((invocation) -> Optional.ofNullable(invocation.getArgument(0)));
		
		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.id-movimiento", is(1)));
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento, DTO nulo - Escenario de error 1.")
	void test_actualizar_movimiento_error_1() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 1)
			   .contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException))
			    .andExpect(result -> assertEquals("Required request body is missing: public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.updateMovimiento(java.lang.Long,com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento, Catalogo Operación no permitido - Escenario de error 2.")
	void test_actualizar_movimiento_error_2() throws Exception {
		// given
		dto.setIdCatalogoOperacion(-1);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 1)
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.updateMovimiento(java.lang.Long,com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'idCatalogoOperacion': rejected value [-1]; codes [Positive.movimientoDTO.idCatalogoOperacion,Positive.idCatalogoOperacion,Positive.long,Positive]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.idCatalogoOperacion,idCatalogoOperacion]; arguments []; default message [idCatalogoOperacion]]; default message [El identificador de operación debe ser mayor a 0.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento, Catalogo Movimiento no permitido - Escenario de error 3.")
	void test_actualizar_movimiento_error_3() throws Exception {
		// given
		dto.setIdCatalogoMovimiento(0);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 1)
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.updateMovimiento(java.lang.Long,com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'idCatalogoMovimiento': rejected value [0]; codes [Positive.movimientoDTO.idCatalogoMovimiento,Positive.idCatalogoMovimiento,Positive.long,Positive]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.idCatalogoMovimiento,idCatalogoMovimiento]; arguments []; default message [idCatalogoMovimiento]]; default message [El identificador de movimiento debe ser mayor a 0.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento, Monto nulo - Escenario de error 4.")
	void test_actualizar_movimiento_error_4() throws Exception {
		// given
		dto.setMonto(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 1)
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.updateMovimiento(java.lang.Long,com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'monto': rejected value [null]; codes [NotNull.movimientoDTO.monto,NotNull.monto,NotNull.java.lang.Double,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.monto,monto]; arguments []; default message [monto]]; default message [El monto es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento, Monto cero - Escenario de error 5.")
	void test_actualizar_movimiento_error_5() throws Exception {
		// given
		dto.setMonto(0D);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 1)
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.updateMovimiento(java.lang.Long,com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'monto': rejected value [0.0]; codes [Positive.movimientoDTO.monto,Positive.monto,Positive.java.lang.Double,Positive]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.monto,monto]; arguments []; default message [monto]]; default message [El monto debe ser mayor a 0.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento, Prestamo nulo - Escenario de error 6.")
	void test_actualizar_movimiento_error_6() throws Exception {
		// given
		dto.setIdPrestamo(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 1)
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.updateMovimiento(java.lang.Long,com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'idPrestamo': rejected value [null]; codes [NotBlank.movimientoDTO.idPrestamo,NotBlank.idPrestamo,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.idPrestamo,idPrestamo]; arguments []; default message [idPrestamo]]; default message [El numero de prestamo es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento, Prestamo vac\u00EDo - Escenario de error 7.")
	void test_actualizar_movimiento_error_7() throws Exception {
		// given
		dto.setIdPrestamo(" ");

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 1)
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.updateMovimiento(java.lang.Long,com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'idPrestamo': rejected value [ ]; codes [NotBlank.movimientoDTO.idPrestamo,NotBlank.idPrestamo,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.idPrestamo,idPrestamo]; arguments []; default message [idPrestamo]]; default message [El numero de prestamo es requerido.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento, Fecha Movimiento nula - Escenario de error 8.")
	void test_actualizar_movimiento_error_8() throws Exception {
		// given
		dto.setFechaMovimiento(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 1)
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.MovimientoDTO com.sosa.controller.MovimientoController.updateMovimiento(java.lang.Long,com.sosa.model.dto.MovimientoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'movimientoDTO' on field 'fechaMovimiento': rejected value [null]; codes [NotNull.movimientoDTO.fechaMovimiento,NotNull.fechaMovimiento,NotNull.java.util.Date,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [movimientoDTO.fechaMovimiento,fechaMovimiento]; arguments []; default message [fechaMovimiento]]; default message [La fecha del movimiento es requerida.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento, Objeto actualizado vacío - Escenario de error 9.")
	void test_actualizar_movimiento_error_9() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		
		// given
		given(movimientoService.updateMovimiento(any(MovimientoDTO.class))).willReturn(Optional.empty());

		// when
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 1)
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().is5xxServerError())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP500Exception))
			    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_M_013, result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_013)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepci\u00F3n cu\u00E1ndo no coincide el identificador del objeto a actualizar.")
	void test_actualizar_movimiento_error_10() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		// given
		
		// when
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", "000002")
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP400Exception))
		    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_M_005, result.getResolvedException().getMessage()))
		    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_005)))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento, id excede longitud permitida - Escenario de error 11.")
	void test_actualizar_movimiento_error_11() throws Exception {
		// given

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 9223372036854775808D)
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
			    .andExpect(result -> assertEquals("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; nested exception is java.lang.NumberFormatException: For input string: \"9.223372036854776E18\"", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_005)));
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento, No existe el movimiento - Escenario de error 12.")
	void test_actualizar_movimiento_error_12() throws Exception{
		// given
		when(movimientoService.updateMovimiento(any(MovimientoDTO.class))).thenThrow(HTTP404Exception.class);
		
		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 1)
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		//then
		response.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP404Exception))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción de tipo HTTP400Exception - intento de modificación de datos inmutables.")
	void test_actualizar_movimiento_error_13() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(movimientoService.updateMovimiento(any(MovimientoDTO.class))).thenThrow(HTTP400Exception.class);
		
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 1)
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción cuándo la DB es inalcanzable.")
	void test_actualizar_movimiento_error_14() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(movimientoService.updateMovimiento(any(MovimientoDTO.class))).thenThrow(CannotCreateTransactionException.class);
		
		ResultActions response = mockMvc.perform(put("/prestamos/v1/movimientos/{id}", 1)
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CannotCreateTransactionException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Test para listar todos los movimientos.")
	void test_listar_movimientos() throws Exception{
		PagingDTO dto = new PagingDTO();
			dto.setPage(0);
			dto.setSize(2);
			dto.setOrder(Direction.ASC);
			dto.setProperty("idMovimiento");
		
		// given
		List<MovimientoDTO> listaMovimientos = new ArrayList<>();
		listaMovimientos.add(MovimientoDTO.builder().monto(3000D).fechaMovimiento(new Date()).fechaRegistro(new Date()).usuarioRegistra(USER_ALTA).build());
		listaMovimientos.add(MovimientoDTO.builder().monto(4000D).fechaMovimiento(new Date()).fechaRegistro(new Date()).usuarioRegistra(USER_ALTA).build());
		listaMovimientos.add(MovimientoDTO.builder().monto(5000D).fechaMovimiento(new Date()).fechaRegistro(new Date()).usuarioRegistra(USER_ALTA).build());
		listaMovimientos.add(MovimientoDTO.builder().monto(6000D).fechaMovimiento(new Date()).fechaRegistro(new Date()).usuarioRegistra(USER_ALTA).build());
		listaMovimientos.add(MovimientoDTO.builder().monto(7000D).fechaMovimiento(new Date()).fechaRegistro(new Date()).usuarioRegistra(USER_ALTA).build());
		PageImpl<MovimientoDTO> page = new PageImpl<MovimientoDTO>(listaMovimientos, PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).ascending()), 5);
		
		Link l[] = new Link[] {
				Link.of("http://192.168.100.65:8092/prestamos/v1/movimientos?page=0&size=2&sort=idMovimiento,asc"),
				Link.of("http://192.168.100.65:8092/prestamos/v1/movimientos?page=0&size=2&sort=idMovimiento,asc"),
				Link.of("http://192.168.100.65:8092/prestamos/v1/movimientos?page=1&size=2&sort=idMovimiento,asc"),
				Link.of("http://192.168.100.65:8092/prestamos/v1/movimientos?page=2&size=2&sort=idMovimiento,asc")};
		PagedModel<MovimientoDTO> model = PagedModel.of(listaMovimientos, new PageMetadata(2, 0, 5, 3), l);
		
		given(movimientoService.findAllMovimientos(any(PagingDTO.class))).willReturn(page);
		given(pagedResourcesAssembler.toModel(any(Page.class), any(MovimientoModelAssembler.class))).willReturn(model);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/movimientos")
				.param("page", dto.getPage().toString())
				.param("size", dto.getSize().toString())
				.param("property", dto.getProperty())
				.param("sortOrder", dto.getOrder().toString()));
		
		// then
		response.andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.page.size", is(2)))
		        .andExpect(jsonPath("$.page.number", is(0)))
		        .andExpect(jsonPath("$.page.totalElements", is(5)))
		        .andExpect(jsonPath("$.page.totalPages", is(3)));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Test para listar movimientos - lista vacía.")
	void test_listar_movimientos_error_1() throws Exception{
		PagingDTO dto = new PagingDTO();
			dto.setPage(0);
			dto.setSize(2);
			dto.setOrder(Direction.ASC);
			dto.setProperty("idMovimiento");
		
		// given
		List<MovimientoDTO> listaMovimientos = new ArrayList<>();
		PageImpl<MovimientoDTO> page = new PageImpl<MovimientoDTO>(listaMovimientos, PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).ascending()), 0);
		
		Link l[] = new Link[] {
				Link.of("http://192.168.100.65:8092/prestamos/v1/movimientos?page=0&size=2&sort=idMovimiento,asc")};
		PagedModel<MovimientoDTO> model = PagedModel.of(listaMovimientos, new PageMetadata(2, 0, 0, 0), l);
		
		given(movimientoService.findAllMovimientos(any(PagingDTO.class))).willReturn(page);
		given(pagedResourcesAssembler.toModel(any(Page.class), any(MovimientoModelAssembler.class))).willReturn(model);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/movimientos")
				.param("page", dto.getPage().toString())
				.param("size", dto.getSize().toString())
				.param("property", dto.getProperty())
				.param("sortOrder", dto.getOrder().toString()));
		
		// then
		response.andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.page.size", is(2)))
		        .andExpect(jsonPath("$.page.number", is(0)))
		        .andExpect(jsonPath("$.page.totalElements", is(0)))
		        .andExpect(jsonPath("$.page.totalPages", is(0)));
	}
	
	@Test
	@DisplayName("Test para manejar una excepción en la capa de servicio.")
	void test_listar_movimientos_error_2() throws Exception{
		// given
		when(movimientoService.findAllMovimientos(any(PagingDTO.class))).thenThrow(RuntimeException.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/movimientos")
			.param("page", "0")
			.param("size", "2")
			.param("property", "idMovimiento")
			.param("sortOrder", Direction.ASC.toString()));
		
		// then
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof RuntimeException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para listar movimientos, n\u00FAmero de p\u00E1gina menor a 0 - Escenario de error 3.")
	void test_listar_movimientos_error_3() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/movimientos")
			.param("page", "-1")
			.param("size", "2")
			.param("property", "idMovimiento")
			.param("sortOrder", Direction.ASC.toString()));
		
		// then
		response.andDo(print())
	        .andExpect(status().isBadRequest())
		    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
		    .andExpect(result -> assertEquals("getAllMovimientos.page: El numero de p\u00E1gina debe ser mayor o igual a cero.", result.getResolvedException().getMessage()))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
		    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_010)));
	}
	
	@Test
	@DisplayName("Test para listar movimientos, tama\u00E1o de p\u00E1gina menor a 0 - Escenario de error 4.")
	void test_listar_movimientos_error_4() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/movimientos")
				.param("page", "0")
				.param("size", "-1")
				.param("property", "idMovimiento")
				.param("sortOrder", Direction.ASC.toString()));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
			    .andExpect(result -> assertEquals("getAllMovimientos.size: El tama\u00F1o de p\u00E1gina debe ser mayor o igual a cero.", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_010)));
	}

	@Test
	@DisplayName("Test para obtener un movimiento.")
	void test_obtener_movimiento() throws Exception {
		// given
		given(movimientoService.findMovimiento(anyLong())).willReturn(Optional.of(dto));
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/movimientos/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.monto", is(50000D)))
		        .andExpect(jsonPath("$.usuario-registra", is(USER_ALTA)))
		        .andExpect(jsonPath("$.fecha-registro").value("2024-01-01T16:10:30"))
		        .andExpect(jsonPath("$.activo").value("true"));
	}
	
	@Test
	@DisplayName("Test para obtener un movimiento, id excede longitud permitida - Escenario de error 1.")
	void test_obtener_movimiento_error_1() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/movimientos/{id}", 9223372036854775808D));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
			    .andExpect(result -> assertEquals("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; nested exception is java.lang.NumberFormatException: For input string: \"9.223372036854776E18\"", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_004)));
	}
	
	@Test
	@DisplayName("Test para obtener un movimiento, no existe el movimiento - Escenario de error 2.")
	void test_obtener_movimiento_error_2() throws Exception{
		// given
		when(movimientoService.findMovimiento(anyLong())).thenThrow(HTTP404Exception.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/movimientos/{id}", 1));
		
		// then
		response.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP404Exception))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("Test para obtener un movimiento, no existe el movimiento - Escenario de error 3.")
	void test_obtener_movimiento_error_3() throws Exception{
		// given
		given(movimientoService.findMovimiento(anyLong())).willReturn(Optional.empty());

		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/movimientos/{id}", 1));
		
		// then
		response.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP404Exception))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción cuándo la DB es inalcanzable.")
	void test_obtener_movimiento_error_4() throws Exception{
		// given
		when(movimientoService.findMovimiento(anyLong())).thenThrow(CannotCreateTransactionException.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/movimientos/{id}", 1));
		
		// then
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CannotCreateTransactionException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para eliminar un movimiento.")
	void test_eliminar_movimiento() throws Exception{
		// given
		dto.setActivo(false);
		given(movimientoService.deleteMovimiento(1)).willReturn(Optional.of(dto));
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/movimientos/{id}", 1));
		
		// then
		response.andDo(print()).andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.id-movimiento", is(1)))
			.andExpect(jsonPath("$.activo", is(false)));
	}
	
	@Test
	@DisplayName("Test para eliminar un movimiento, id nulo - Escenario de error 1.")
	void test_eliminar_movimiento_error_1() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/movimientos"));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpRequestMethodNotSupportedException))
			    .andExpect(result -> assertEquals("Request method 'DELETE' not supported", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_008)));
	}
	
	@Test
	@DisplayName("Test para eliminar un movimiento, id vacío - Escenario de error 2.")
	void test_eliminar_movimiento_error_2() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/movimientos/{id}", ""));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpRequestMethodNotSupportedException))
			    .andExpect(result -> assertEquals("Request method 'DELETE' not supported", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_008)));
	}
	
	@Test
	@DisplayName("Test para eliminar un movimiento, id excede longitud permitida - Escenario de error 3.")
	void test_eliminar_movimiento_error_3() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/movimientos/{id}", 9223372036854775808D));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
			    .andExpect(result -> assertEquals("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; nested exception is java.lang.NumberFormatException: For input string: \"9.223372036854776E18\"", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_008)));
	}
	
	@Test
	@DisplayName("Test para eliminar un movimiento, error en capa de servicio - Escenario de error 4.")
	void test_eliminar_movimiento_error_4() throws Exception {
		// given
		given(movimientoService.deleteMovimiento(anyLong())).willReturn(Optional.empty());

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/movimientos/{id}", 1)
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
        .andExpect(status().is5xxServerError())
	    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP500Exception))
	    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_M_014, result.getResolvedException().getMessage()))
	    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_M_014)))
	    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para eliminar un movimiento, no existe el movimiento - Escenario de error 5.")
	void test_eliminar_movimiento_error_5() throws Exception{
		// given
		when(movimientoService.deleteMovimiento(anyLong())).thenThrow(HTTP404Exception.class);
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/movimientos/{id}", 1));
		
		// then
		response.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP404Exception))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción cuándo la DB es inalcanzable.")
	void test_eliminar_movimiento_error_6() throws Exception{
		// given
		when(movimientoService.deleteMovimiento(anyLong())).thenThrow(CannotCreateTransactionException.class);
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/movimientos/{id}", 1));
		
		// then
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CannotCreateTransactionException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
}
