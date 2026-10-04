package com.sosa.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.sosa.model.Prestamo;

@Repository
public interface PrestamoRepository extends PagingAndSortingRepository<Prestamo, String>{
	
}
