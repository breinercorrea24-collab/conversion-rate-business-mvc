package com.bca.conversion_business_service.controller.mapper;

import com.bca.conversion_business_service.dto.ClienteDTO;
import com.bca.conversion_business_service.dto.CotizacionDTO;
import com.bca.conversion_business_service.dto.SimulacionDTO;
import com.bca.conversion_business_service.entity.Cliente;
import com.bca.conversion_business_service.entity.Cotizacion;
import com.bca.conversion_business_service.entity.Simulacion;
import com.bca.conversion_business_service.service.mapper.Mapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MapperTest {

    @Test
    void instantiateMapper_hasNonNullInstance() {
        Mapper m = new Mapper();
        assertNotNull(m);
    }

	@Test
	void convertirACliente_and_back() {
		ClienteDTO dto = new ClienteDTO(1L, "Natural", "12345678", "Ana", "Perez", 0.05, null, null);

		Cliente cliente = Mapper.convertirACliente(dto);
		assertNotNull(cliente);
		assertEquals(dto.getIdCliente(), cliente.getIdCliente());
		assertEquals(dto.getDni(), cliente.getDni());
		assertEquals(dto.getNombre(), cliente.getNombre());

		ClienteDTO dto2 = Mapper.convertirAClienteDTO(cliente);
		assertNotNull(dto2);
		assertEquals(cliente.getIdCliente(), dto2.getIdCliente());
		assertEquals(cliente.getNombre(), dto2.getNombre());
		assertEquals(cliente.getApellidos(), dto2.getApellidos());
	}

	@Test
	void convertirSimulacion_and_back_and_list() {
		Simulacion simulacion = new Simulacion(11L, 2L, 300.0, "USD", "PEN", LocalDate.of(2025,11,20), 3.5, 0.1, 1050.0);

		SimulacionDTO dto = Mapper.convertirASimulacionDTO(simulacion);
		assertNotNull(dto);
		assertEquals(simulacion.getIdSimulacion(), dto.getIdSimulacion());
		assertEquals(simulacion.getAmount(), dto.getAmount());

		Simulacion back = Mapper.convertirASimulacion(dto);
		assertNotNull(back);
		assertEquals(dto.getIdSimulacion(), back.getIdSimulacion());
		assertEquals(dto.getCurrency(), back.getCurrency());

		List<SimulacionDTO> list = Mapper.convertirASimulacionDTOList(List.of(simulacion));
		assertEquals(1, list.size());
		assertEquals(simulacion.getIdSimulacion(), list.get(0).getIdSimulacion());
	}

	@Test
	void convertirCotizacion_and_back_and_list() {
		Cotizacion cot = new Cotizacion(21L, 11L, 2L, new BigDecimal("150.00"), "USD", "PEN", LocalDate.of(2025,11,20), "NEW");

		CotizacionDTO dto = Mapper.convertirACotizacionDTO(cot);
		assertNotNull(dto);
		assertEquals(cot.getIdCotizacion(), dto.getIdCotizacion());
		assertEquals(cot.getAmount(), dto.getAmount());

		Cotizacion back = Mapper.convertirACotizacion(dto);
		assertNotNull(back);
		assertEquals(dto.getIdCotizacion(), back.getIdCotizacion());
		assertEquals(dto.getStatus(), back.getStatus());

		List<CotizacionDTO> list = Mapper.convertirACotizacionDTOList(List.of(cot));
		assertEquals(1, list.size());
		assertEquals(cot.getIdCotizacion(), list.get(0).getIdCotizacion());
	}
}
