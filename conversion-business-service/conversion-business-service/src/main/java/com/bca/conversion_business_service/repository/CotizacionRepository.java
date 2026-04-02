package com.bca.conversion_business_service.repository;


import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.bca.conversion_business_service.entity.Cotizacion;

import reactor.core.publisher.Flux;

@Repository
public interface CotizacionRepository extends R2dbcRepository<Cotizacion, Long> {
    Flux<Cotizacion> findByIdCliente(Long idCliente);
}
