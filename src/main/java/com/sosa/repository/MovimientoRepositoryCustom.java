package com.sosa.repository;

import com.sosa.model.CatalogoMovimiento;
import com.sosa.model.CatalogoOperacion;
import com.sosa.model.Cliente;

public interface MovimientoRepositoryCustom {
	
	void saveMovimiento(Cliente cliente, CatalogoMovimiento movimiento, CatalogoOperacion operacion);

}
