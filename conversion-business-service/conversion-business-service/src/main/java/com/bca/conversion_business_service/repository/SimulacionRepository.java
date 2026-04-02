package com.bca.conversion_business_service.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.bca.conversion_business_service.entity.Simulacion;

import reactor.core.publisher.Flux;

@Repository
public interface SimulacionRepository extends R2dbcRepository<Simulacion, Long> {
    Flux<Simulacion> findByIdCliente(Long idCliente);
}
