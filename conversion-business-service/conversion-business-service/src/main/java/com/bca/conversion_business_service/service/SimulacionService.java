package com.bca.conversion_business_service.service;


import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import com.bca.conversion_business_service.dto.SimulacionDTO;
import com.bca.conversion_business_service.entity.Simulacion;
import com.bca.conversion_business_service.repository.SimulacionRepository;
import com.bca.conversion_business_service.service.mapper.Mapper;

import reactor.core.publisher.Mono;

@Service
public class SimulacionService {
    private final SimulacionRepository simulacionRepository;

    public SimulacionService(SimulacionRepository simulacionRepository) {
        this.simulacionRepository = simulacionRepository;
    }

    public Mono<SimulacionDTO> saveSimulacion(SimulacionDTO simulacionDTO) {

        Consumer<Simulacion> auditConsumer = s -> {
            // llamar a una cache.
            System.out.println("AUDIT: saved simulacion id=" + s.getIdSimulacion());
        };

    	System.out.println("SimulacionDTO saved: " + simulacionDTO);
        Simulacion simulacion = Mapper.convertirASimulacion(simulacionDTO);

        return simulacionRepository.save(simulacion)
            .doOnNext(auditConsumer)
            .map(Mapper::convertirASimulacionDTO);
    }
}
