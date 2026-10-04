package com.sosa.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Metrics;

@Aspect
@Configuration
public class ControllerLayerAspects {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerLayerAspects.class);

	@Before("execution (public * com.sosa.controller.*Controller.*(..))")
	public void logBeforeRestCall(JoinPoint join) throws Throwable {
		LOGGER.info("+---- AOP Before REST call ----+" + join);
	}

	@AfterReturning("execution(public * com.sosa.controller.MovimientoController.createMovimiento(..))")
	public void afterCallingCreateTransaction(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning Create REST call ----+" + join);
		Metrics.counter("create_transaction").increment();
	}

	@AfterReturning("execution(public * com.sosa.controller.MovimientoController.getAllMovimientos(..))")
	public void afterCallingGetAllTransaction(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning get transactions REST call ----+" + join);
		Metrics.counter("get_transactions").increment();
	}

	@AfterReturning("execution(public * com.sosa.controller.MovimientoController.getMovimiento(..))")
	public void afterCallingGetTransaction(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning get transaction REST call ----+" + join);
		Metrics.counter("get_transaction").increment();
	}

	@AfterReturning("execution(public * com.sosa.controller.MovimientoController.updateMovimiento(..))")
	public void afterCallingUpdateTransaction(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning update transaction REST call ----+" + join);
		Metrics.counter("update_transaction").increment();
	}
	
	@AfterReturning("execution(public * com.sosa.controller.MovimientoController.deleteMovimiento(..))")
	public void afterCallingDeleteTransaction(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning delete transaction REST call ----+" + join);
		Metrics.counter("delete_transaction").increment();
	}

	@AfterThrowing(pointcut = "execution(public * com.sosa.controller.MovimientoController.*(..))", throwing = "e")
	public void afterGetGreetingThrowsException(Exception e) {
		Metrics.counter("transaction_errors").increment();
	}
}
