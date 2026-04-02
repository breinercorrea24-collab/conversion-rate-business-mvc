package com.bca.conversion_business_service.api;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import com.bca.conversion_business_service.api.dto.ExchangeRateDTO;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyRateClientTest {

    @Test
    void testGetRate_withMockWebServer_returnsParsedDtoAndCorrectPath() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.start();

            String baseUrl = server.url("/").toString();
            WebClient webClient = WebClient.builder()
                    .baseUrl(baseUrl)
                    .build();

            CurrencyRateClient client = new CurrencyRateClient(webClient);

            // Prepare mock JSON response matching ExchangeRateDTO fields
            String json = "{\"from\":\"PEN\",\"to\":\"EUR\",\"amount\":100.0,\"rate\":0.254064,\"converted\":25.4064}";

            server.enqueue(new MockResponse()
                    .setBody(json)
                    .addHeader("Content-Type", "application/json"));

            // Act
            Mono<ExchangeRateDTO> result = client.getRate("EUR", "PEN", 100.0);

            // Assert response content
            StepVerifier.create(result)
                    .assertNext(dto -> {
                        assertEquals("PEN", dto.getFrom());
                        assertEquals("EUR", dto.getTo());
                        assertEquals(100.0, dto.getAmount());
                        assertEquals(0.254064, dto.getRate());
                    })
                    .verifyComplete();

            // Verify the request path and query parameters
            RecordedRequest recorded = server.takeRequest();
            String path = recorded.getPath();
            assertTrue(path.startsWith("/api/v1/rates"), "Request should call /api/v1/rates");
            // Basic check for query params presence
            assertTrue(path.contains("to=EUR"));
            assertTrue(path.contains("from=PEN"));
            assertTrue(path.contains("amount=100.0") || path.contains("amount=100"));
        }
    }

    @Test
    void testGetRate_serverError_returnsFallback() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.start();

            String baseUrl = server.url("/").toString();
            WebClient webClient = WebClient.builder()
                    .baseUrl(baseUrl)
                    .build();

            CurrencyRateClient client = new CurrencyRateClient(webClient);

            server.enqueue(new MockResponse().setResponseCode(500).setBody("internal error"));

            Mono<ExchangeRateDTO> result = client.getRate("EUR", "PEN", 100.0);

            StepVerifier.create(result)
                    .assertNext(dto -> {
                        assertEquals("PEN", dto.getFrom());
                        assertEquals("EUR", dto.getTo());
                        assertEquals(1.0, dto.getRate());
                        assertEquals(100.0, dto.getConverted());
                        assertEquals("No se pudo obtener el tipo de cambio. Servicio no disponible.", dto.getMessage());
                    })
                    .verifyComplete();
        }
    }
}
