package com.bca.conversion_business_service.service;

import com.bca.conversion_business_service.dto.ClienteDTO;
import com.bca.conversion_business_service.entity.Cliente;
import com.bca.conversion_business_service.entity.Cotizacion;
import com.bca.conversion_business_service.entity.Simulacion;
import com.bca.conversion_business_service.repository.ClienteRepository;
import com.bca.conversion_business_service.repository.CotizacionRepository;
import com.bca.conversion_business_service.repository.SimulacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

	@Mock
	ClienteRepository clienteRepository;

	@Mock
	CotizacionRepository cotizacionRepository;

	@Mock
	SimulacionRepository simulacionRepository;

	@InjectMocks
	ClienteService clienteService;

	@Test
	void crearOActualizarCliente_whenClienteExists_updatesAndReturnsDto() {
		ClienteDTO dto = new ClienteDTO();
		dto.setIdCliente(null);
		dto.setTipoCliente("Natural");
		dto.setDni("12345678");
		dto.setNombre("John");
		dto.setApellidos("Doe");

		Cliente existing = new Cliente(1L, "NATURAL", "12345678", "OldName", "OldLast", 0.02);

		when(clienteRepository.findByDni(dto.getDni())).thenReturn(Mono.just(existing));
		when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> Mono.just((Cliente) invocation.getArgument(0)));

		StepVerifier.create(clienteService.crearOActualizarCliente(dto))
				.assertNext(result -> {
					// nombre y apellidos actualizados
					assert result.getNombre().equals("John");
					assert result.getApellidos().equals("Doe");
					// tasaPreferencial calculada a partir del DNI y tipo
					// Para dni 12345678 y tipo Natural la tasa esperada es 0.02 + 8*0.01 = 0.1
					assert result.getTasaPreferencial() != null;
					double expected = 0.02 + 8 * 0.01;
					org.junit.jupiter.api.Assertions.assertEquals(expected, result.getTasaPreferencial(), 1e-9);
				})
				.verifyComplete();
	}

	@Test
	void crearOActualizarCliente_whenClienteDoesNotExist_createsNew() {
		ClienteDTO dto = new ClienteDTO();
		dto.setIdCliente(null);
		dto.setTipoCliente("Juridica");
		dto.setDni("87654321");
		dto.setNombre("ACME");
		dto.setApellidos("Corp");

		when(clienteRepository.findByDni(dto.getDni())).thenReturn(Mono.empty());
		when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
			Cliente saved = (Cliente) invocation.getArgument(0);
			saved.setIdCliente(42L);
			return Mono.just(saved);
		});

		StepVerifier.create(clienteService.crearOActualizarCliente(dto))
				.assertNext(result -> {
					org.junit.jupiter.api.Assertions.assertEquals(42L, result.getIdCliente());
					org.junit.jupiter.api.Assertions.assertEquals("ACME", result.getNombre());
					// Para tipo Juridica: base 0.01 + lastDigit(1)*0.01 = 0.02
					double expected = 0.01 + 1 * 0.01; // dni 87654321 -> char at index7 = '1'
					org.junit.jupiter.api.Assertions.assertEquals(expected, result.getTasaPreferencial(), 1e-9);
				})
				.verifyComplete();
	}

	@Test
	void obtenerClienteConDetalles_returnsClienteWithCotizacionesAndSimulaciones() {
		Long id = 100L;
		Cliente cliente = new Cliente(id, "NATURAL", "12345678", "Alice", "Smith", 0.05);

		Cotizacion cot = new Cotizacion(10L, 20L, id, new BigDecimal("100.0"), "USD", "PEN", LocalDate.now(), "NEW");
		Simulacion sim = new Simulacion(30L, id, 200.0, "USD", "PEN", LocalDate.now(), 3.5, 0.1, 700.0);

		when(clienteRepository.findById(id)).thenReturn(Mono.just(cliente));
		when(cotizacionRepository.findByIdCliente(id)).thenReturn(Flux.just(cot));
		when(simulacionRepository.findByIdCliente(id)).thenReturn(Flux.just(sim));

		StepVerifier.create(clienteService.obtenerClienteConDetalles(id))
				.assertNext(dto -> {
					org.junit.jupiter.api.Assertions.assertEquals(id, dto.getIdCliente());
					org.junit.jupiter.api.Assertions.assertNotNull(dto.getCotizaciones());
					org.junit.jupiter.api.Assertions.assertEquals(1, dto.getCotizaciones().size());
					org.junit.jupiter.api.Assertions.assertNotNull(dto.getSimulaciones());
					org.junit.jupiter.api.Assertions.assertEquals(1, dto.getSimulaciones().size());
				})
				.verifyComplete();
	}

}
