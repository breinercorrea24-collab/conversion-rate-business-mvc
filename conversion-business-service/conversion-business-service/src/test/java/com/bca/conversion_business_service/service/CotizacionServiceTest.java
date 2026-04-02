package com.bca.conversion_business_service.service;

import com.bca.conversion_business_service.dto.CotizacionDTO;
import com.bca.conversion_business_service.entity.Cotizacion;
import com.bca.conversion_business_service.repository.CotizacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CotizacionServiceTest {

    @Mock
    CotizacionRepository cotizacionRepository;

    @InjectMocks
    CotizacionService cotizacionService;

    @Test
    void saveCotizacion_shouldMapAndReturnDto() {
        CotizacionDTO dto = new CotizacionDTO();
        dto.setIdCotizacion(null);
        dto.setIdSimulacion(5L);
        dto.setAmount(new BigDecimal("150.00"));
        dto.setCurrency("USD");
        dto.setTargetCurrency("PEN");
        dto.setDate(LocalDate.of(2025, 11, 20));
        dto.setStatus("NEW");

        // simulate repository saving and returning entity with an id
        when(cotizacionRepository.save(any(Cotizacion.class))).thenAnswer(invocation -> {
            Cotizacion arg = invocation.getArgument(0);
            arg.setIdCotizacion(99L);
            return Mono.just(arg);
        });

        StepVerifier.create(cotizacionService.saveCotizacion(dto))
                .assertNext(result -> {
                    org.junit.jupiter.api.Assertions.assertEquals(99L, result.getIdCotizacion());
                    org.junit.jupiter.api.Assertions.assertEquals(dto.getIdSimulacion(), result.getIdSimulacion());
                    org.junit.jupiter.api.Assertions.assertEquals(dto.getAmount(), result.getAmount());
                    org.junit.jupiter.api.Assertions.assertEquals(dto.getCurrency(), result.getCurrency());
                    org.junit.jupiter.api.Assertions.assertEquals(dto.getTargetCurrency(), result.getTargetCurrency());
                    org.junit.jupiter.api.Assertions.assertEquals(dto.getDate(), result.getDate());
                    org.junit.jupiter.api.Assertions.assertEquals(dto.getStatus(), result.getStatus());
                })
                .verifyComplete();
    }
}
