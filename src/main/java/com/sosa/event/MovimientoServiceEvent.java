package com.sosa.event;

import org.springframework.context.ApplicationEvent;

import com.sosa.model.dto.MovimientoDTO;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class MovimientoServiceEvent extends ApplicationEvent {

	private static final long serialVersionUID = -6121624308782503383L;
	
	private MovimientoDTO movimiento;
	private String eventType;

	public MovimientoServiceEvent(Object source, String eventType, MovimientoDTO movimiento) {
		super(source);
		this.eventType = eventType;
		this.movimiento = movimiento;
	}

	@Override
	public String toString() {
		return String.format("MovimientoServiceEvent [evento = {}, eventType = {}]", movimiento.getIdMovimiento(), eventType);
	}
}