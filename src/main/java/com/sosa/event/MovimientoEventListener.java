package com.sosa.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class MovimientoEventListener implements ApplicationListener<MovimientoServiceEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MovimientoEventListener.class);

	public void onApplicationEvent(MovimientoServiceEvent event) {
		MovimientoServiceEvent movimientoEvent = event;
		LOGGER.info("Movimiento {} with details : {}", event.getEventType(), movimientoEvent.getMovimiento());
	}
}