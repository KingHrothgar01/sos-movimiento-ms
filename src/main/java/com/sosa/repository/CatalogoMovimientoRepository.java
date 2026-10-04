package com.sosa.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.sosa.model.CatalogoMovimiento;

@Repository
public interface CatalogoMovimientoRepository extends PagingAndSortingRepository<CatalogoMovimiento, Long>{
	
}
