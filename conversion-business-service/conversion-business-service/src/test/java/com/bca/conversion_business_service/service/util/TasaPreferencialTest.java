package com.bca.conversion_business_service.service.util;

import com.bca.conversion_business_service.dto.ClienteDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TasaPreferencialTest {

	@Test
	void validateRate_naturalClient_numericLastDigit() {
		ClienteDTO c = new ClienteDTO();
		c.setTipoCliente("Natural");
		c.setDni("12345678");

		double rate = TasaPreferencial.validateRate(c);
		double expected = 0.02 + 8 * 0.01; // base for Natural is 0.02
		assertEquals(expected, rate, 1e-9);
	}

	@Test
	void validateRate_juridicaClient_numericLastDigit() {
		ClienteDTO c = new ClienteDTO();
		c.setTipoCliente("Juridica");
		c.setDni("87654321");

		double rate = TasaPreferencial.validateRate(c);
		double expected = 0.01 + 1 * 0.01; // base for Juridica is 0.01
		assertEquals(expected, rate, 1e-9);
	}

	@Test
	void validateRate_invalidShortDni_throws() {
		ClienteDTO c = new ClienteDTO();
		c.setTipoCliente("Natural");
		c.setDni("123");

		assertThrows(IllegalArgumentException.class, () -> TasaPreferencial.validateRate(c));
	}

	@Test
	void validateRate_nullDni_throws() {
		ClienteDTO c = new ClienteDTO();
		c.setTipoCliente("Natural");
		c.setDni(null);

		assertThrows(IllegalArgumentException.class, () -> TasaPreferencial.validateRate(c));
	}

	@Test
	void validateRate_alphaLastChar_isParsedByCharacterNumericValue() {
		ClienteDTO c = new ClienteDTO();
		c.setTipoCliente("Natural");
		c.setDni("1234567A"); // charAt(7) == 'A' -> numeric value 10

		double rate = TasaPreferencial.validateRate(c);
		double expected = 0.02 + 10 * 0.01; // 0.02 + 0.10 = 0.12
		assertEquals(expected, rate, 1e-9);
	}
}
