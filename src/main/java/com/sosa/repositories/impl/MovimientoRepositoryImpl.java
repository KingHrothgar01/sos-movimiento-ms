package com.sosa.repositories.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.sosa.model.CatalogoMovimiento;
import com.sosa.model.CatalogoOperacion;
import com.sosa.model.Cliente;
import com.sosa.repository.MovimientoRepositoryCustom;

@Repository
@Transactional
public class MovimientoRepositoryImpl implements MovimientoRepositoryCustom {

	@Override
	public void saveMovimiento(Cliente cliente, CatalogoMovimiento movimiento, CatalogoOperacion operacion) {
		
	}

}