package com.bca.conversion_business_service.service;

import org.springframework.stereotype.Service;

import com.bca.conversion_business_service.dto.CotizacionDTO;
import com.bca.conversion_business_service.entity.Cotizacion;
import com.bca.conversion_business_service.repository.CotizacionRepository;
import com.bca.conversion_business_service.service.mapper.Mapper;

import reactor.core.publisher.Mono;

@Service
public class CotizacionService {
	private final CotizacionRepository cotizacionRepository;

	public CotizacionService(CotizacionRepository cotizacionRepository) {
		super();
		this.cotizacionRepository = cotizacionRepository;
	}
	
	public Mono<CotizacionDTO> saveCotizacion(CotizacionDTO cotizacionDTO) {
    	System.out.println("cotizacionDTO saved: " + cotizacionDTO);
    	Cotizacion cotizacion = Mapper.convertirACotizacion(cotizacionDTO);
        System.out.println("Simulacion saved: " + cotizacion);
        return cotizacionRepository.save(cotizacion)
                .map(Mapper::convertirACotizacionDTO);
    }
}
