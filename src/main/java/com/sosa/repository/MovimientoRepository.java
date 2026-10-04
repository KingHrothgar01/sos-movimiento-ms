package com.sosa.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.sosa.model.Movimiento;

@Repository
public interface MovimientoRepository extends PagingAndSortingRepository<Movimiento, Long> {

}
