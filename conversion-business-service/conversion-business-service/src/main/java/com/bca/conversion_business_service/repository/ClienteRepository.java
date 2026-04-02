package com.bca.conversion_business_service.repository;


import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.bca.conversion_business_service.entity.Cliente;

import reactor.core.publisher.Mono;

@Repository
public interface ClienteRepository extends R2dbcRepository<Cliente, Long> {
    // @Query("SELECT * FROM cliente WHERE dni = :dni")
    Mono<Cliente> findByDni(String dni);
}
