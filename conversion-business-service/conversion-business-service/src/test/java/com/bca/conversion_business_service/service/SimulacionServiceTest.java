package com.bca.conversion_business_service.service;

import com.bca.conversion_business_service.dto.SimulacionDTO;
import com.bca.conversion_business_service.entity.Simulacion;
import com.bca.conversion_business_service.repository.SimulacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SimulacionServiceTest {

	@Mock
	SimulacionRepository simulacionRepository;

	@InjectMocks
	SimulacionService simulacionService;

	@Test
	void saveSimulacion_shouldMapAndReturnDto() {
		SimulacionDTO dto = new SimulacionDTO();
		dto.setIdSimulacion(null);
		dto.setIdCliente(7L);
		dto.setAmount(250.0);
		dto.setCurrency("USD");
		dto.setTargetCurrency("PEN");
		dto.setDate(LocalDate.of(2025,11,20));
		dto.setRate(3.5);
		dto.setRatePreferente(0.05);
		dto.setExchangeAmount(875.0);

		when(simulacionRepository.save(any(Simulacion.class))).thenAnswer(invocation -> {
			Simulacion arg = invocation.getArgument(0);
			arg.setIdSimulacion(123L);
			return Mono.just(arg);
		});

		StepVerifier.create(simulacionService.saveSimulacion(dto))
				.assertNext(result -> {
					org.junit.jupiter.api.Assertions.assertEquals(123L, result.getIdSimulacion());
					org.junit.jupiter.api.Assertions.assertEquals(dto.getIdCliente(), result.getIdCliente());
					org.junit.jupiter.api.Assertions.assertEquals(dto.getAmount(), result.getAmount());
					org.junit.jupiter.api.Assertions.assertEquals(dto.getCurrency(), result.getCurrency());
					org.junit.jupiter.api.Assertions.assertEquals(dto.getTargetCurrency(), result.getTargetCurrency());
					org.junit.jupiter.api.Assertions.assertEquals(dto.getDate(), result.getDate());
					org.junit.jupiter.api.Assertions.assertEquals(dto.getRate(), result.getRate());
					org.junit.jupiter.api.Assertions.assertEquals(dto.getRatePreferente(), result.getRatePreferente());
					org.junit.jupiter.api.Assertions.assertEquals(dto.getExchangeAmount(), result.getExchangeAmount());
				})
				.verifyComplete();
	}
}

