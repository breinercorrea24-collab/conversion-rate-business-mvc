package com.bca.conversion_business_service.api;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WebClientConfigTest {

	@Test
	void currencyRateWebClient_shouldBeCreated() {
		WebClientConfig config = new WebClientConfig();
		WebClient client = config.currencyRateWebClient();
		assertNotNull(client, "currencyRateWebClient should not be null");
	}
}
