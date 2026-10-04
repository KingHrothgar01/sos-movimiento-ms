package com.sosa.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.sosa.model.CatalogoOperacion;

@Repository
public interface CatalogoOperacionRepository extends PagingAndSortingRepository<CatalogoOperacion, Long>{
	
}
