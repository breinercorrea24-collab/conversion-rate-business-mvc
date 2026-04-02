package com.bca.currency_rate_service.router;

import com.bca.currency_rate_service.handler.RateHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class RateRouterTests {

    @Autowired
    private WebTestClient client;

    @MockBean
    private RateHandler handler;

    @Test
    void testErrorHandlingReturns500() {
        when(handler.getRate(any(ServerRequest.class)))
            .thenReturn(Mono.error(new RuntimeException("Fallo de prueba")));

        // Ejecutar llamada real (el bean GlobalExceptionHandler debería estar presente y transformar el error)
        client.get()
            .uri("/api/v1/rates")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody()
                .jsonPath("$.message").value(value -> assertTrue(value.toString().contains("Fallo de prueba")));
    }
}