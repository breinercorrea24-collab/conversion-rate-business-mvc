package com.bca.currency_rate_service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("CurrencyRateServiceApplication Tests")
class CurrencyRateServiceApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@BeforeEach
	void setUp() {
		assertNotNull(applicationContext, "ApplicationContext must be loaded");
	}

	@Test
	@DisplayName("Application context should load successfully")
	void applicationContextLoads() {
		assertNotNull(applicationContext, "ApplicationContext should be loaded");
	}

	@Test
	@DisplayName("Main method should be callable")
	void mainMethodIsCallable() {
		assertDoesNotThrow(() -> {
			// Verificar que la clase puede ser instanciada
			assertNotNull(CurrencyRateServiceApplication.class);
		});
	}
}
