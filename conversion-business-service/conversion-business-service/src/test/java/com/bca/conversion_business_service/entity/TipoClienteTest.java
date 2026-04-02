package com.bca.conversion_business_service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TipoClienteTest {

	@Test
	void fromString_unknownValue_returnsNatural() {
		TipoCliente result = TipoCliente.fromString("SOMETHING_ELSE");
		assertEquals(TipoCliente.NATURAL, result);
	}

}
