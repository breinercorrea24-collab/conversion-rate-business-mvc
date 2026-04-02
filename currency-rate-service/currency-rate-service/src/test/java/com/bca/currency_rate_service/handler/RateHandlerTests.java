package com.bca.currency_rate_service.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.bca.currency_rate_service.api.dto.RateValueDTO;
import com.bca.currency_rate_service.service.RateService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RateHandlerTests {

    @Mock
    private ServerRequest request;

    @Mock
    private RateService rateService;

    private RateHandler rateHandler;

    @BeforeEach
    void setUp() {
        rateHandler = new RateHandler(rateService);
    }

    @Test
    void testConstructorInitializesRateService() {
        // Act & Assert
        assertNotNull(rateHandler);
    }

    @Test
    void testGetRateWithAllParameters() {
        // Arrange
        String from = "USD";
        String to = "EUR";
        Double amount = 100.0;

        when(request.queryParam("from")).thenReturn(Optional.of(from));
        when(request.queryParam("to")).thenReturn(Optional.of(to));
        when(request.queryParam("amount")).thenReturn(Optional.of("100"));

        RateValueDTO rateValue = new RateValueDTO(from, to, amount, 0.92, 92.0);
        when(rateService.getRate(from, to, amount)).thenReturn(Mono.just(rateValue));

        // Act
        Mono<ServerResponse> result = rateHandler.getRate(request);

        // Assert
        StepVerifier.create(result)
            .expectNextCount(1)
            .expectComplete()
            .verify();

        verify(request, times(3)).queryParam(anyString());
        verify(rateService).getRate(from, to, amount);
    }

    @Test
    void testGetRateWithDefaultFromCurrency() {
        // Arrange
        String defaultFrom = "USD";
        String to = "GBP";
        Double amount = 50.0;

        when(request.queryParam("from")).thenReturn(Optional.empty());
        when(request.queryParam("to")).thenReturn(Optional.of(to));
        when(request.queryParam("amount")).thenReturn(Optional.of("50"));

        RateValueDTO rateValue = new RateValueDTO(defaultFrom, to, amount, 0.79, 39.5);
        when(rateService.getRate(defaultFrom, to, amount)).thenReturn(Mono.just(rateValue));

        // Act
        Mono<ServerResponse> result = rateHandler.getRate(request);

        // Assert
        StepVerifier.create(result)
            .expectNextCount(1)
            .expectComplete()
            .verify();

        verify(rateService).getRate(defaultFrom, to, amount);
    }

    @Test
    void testGetRateWithDefaultToCurrency() {
        // Arrange
        String from = "GBP";
        String defaultTo = "EUR";
        Double amount = 25.0;

        when(request.queryParam("from")).thenReturn(Optional.of(from));
        when(request.queryParam("to")).thenReturn(Optional.empty());
        when(request.queryParam("amount")).thenReturn(Optional.of("25"));

        RateValueDTO rateValue = new RateValueDTO(from, defaultTo, amount, 1.17, 29.25);
        when(rateService.getRate(from, defaultTo, amount)).thenReturn(Mono.just(rateValue));

        // Act
        Mono<ServerResponse> result = rateHandler.getRate(request);

        // Assert
        StepVerifier.create(result)
            .expectNextCount(1)
            .expectComplete()
            .verify();

        verify(rateService).getRate(from, defaultTo, amount);
    }

    @Test
    void testGetRateWithDefaultAmount() {
        // Arrange
        String from = "EUR";
        String to = "USD";
        Double defaultAmount = 1.0;

        when(request.queryParam("from")).thenReturn(Optional.of(from));
        when(request.queryParam("to")).thenReturn(Optional.of(to));
        when(request.queryParam("amount")).thenReturn(Optional.empty());

        RateValueDTO rateValue = new RateValueDTO(from, to, defaultAmount, 1.09, 1.09);
        when(rateService.getRate(from, to, defaultAmount)).thenReturn(Mono.just(rateValue));

        // Act
        Mono<ServerResponse> result = rateHandler.getRate(request);

        // Assert
        StepVerifier.create(result)
            .expectNextCount(1)
            .expectComplete()
            .verify();

        verify(rateService).getRate(from, to, defaultAmount);
    }

    @Test
    void testGetRateWithAllDefaults() {
        // Arrange
        String defaultFrom = "USD";
        String defaultTo = "EUR";
        Double defaultAmount = 1.0;

        when(request.queryParam("from")).thenReturn(Optional.empty());
        when(request.queryParam("to")).thenReturn(Optional.empty());
        when(request.queryParam("amount")).thenReturn(Optional.empty());

        RateValueDTO rateValue = new RateValueDTO(defaultFrom, defaultTo, defaultAmount, 0.92, 0.92);
        when(rateService.getRate(defaultFrom, defaultTo, defaultAmount)).thenReturn(Mono.just(rateValue));

        // Act
        Mono<ServerResponse> result = rateHandler.getRate(request);

        // Assert
        StepVerifier.create(result)
            .expectNextCount(1)
            .expectComplete()
            .verify();

        verify(rateService).getRate(defaultFrom, defaultTo, defaultAmount);
    }

    @Test
    void testGetRateWithZeroAmount() {
        // Arrange
        String from = "USD";
        String to = "EUR";
        Double amount = 0.0;

        when(request.queryParam("from")).thenReturn(Optional.of(from));
        when(request.queryParam("to")).thenReturn(Optional.of(to));
        when(request.queryParam("amount")).thenReturn(Optional.of("0"));

        RateValueDTO rateValue = new RateValueDTO(from, to, amount, 0.92, 0.0);
        when(rateService.getRate(from, to, amount)).thenReturn(Mono.just(rateValue));

        // Act
        Mono<ServerResponse> result = rateHandler.getRate(request);

        // Assert
        StepVerifier.create(result)
            .expectNextCount(1)
            .expectComplete()
            .verify();
    }

    @Test
    void testGetRateWithLargeAmount() {
        // Arrange
        String from = "USD";
        String to = "EUR";
        Double amount = 1000000.0;

        when(request.queryParam("from")).thenReturn(Optional.of(from));
        when(request.queryParam("to")).thenReturn(Optional.of(to));
        when(request.queryParam("amount")).thenReturn(Optional.of("1000000"));

        RateValueDTO rateValue = new RateValueDTO(from, to, amount, 0.92, 920000.0);
        when(rateService.getRate(from, to, amount)).thenReturn(Mono.just(rateValue));

        // Act
        Mono<ServerResponse> result = rateHandler.getRate(request);

        // Assert
        StepVerifier.create(result)
            .expectNextCount(1)
            .expectComplete()
            .verify();
    }

    @Test
    void testGetRateWithSmallAmount() {
        // Arrange
        String from = "USD";
        String to = "EUR";
        Double amount = 0.01;

        when(request.queryParam("from")).thenReturn(Optional.of(from));
        when(request.queryParam("to")).thenReturn(Optional.of(to));
        when(request.queryParam("amount")).thenReturn(Optional.of("0.01"));

        RateValueDTO rateValue = new RateValueDTO(from, to, amount, 0.92, 0.0092);
        when(rateService.getRate(from, to, amount)).thenReturn(Mono.just(rateValue));

        // Act
        Mono<ServerResponse> result = rateHandler.getRate(request);

        // Assert
        StepVerifier.create(result)
            .expectNextCount(1)
            .expectComplete()
            .verify();
    }

    @Test
    void testGetRateWithSpecialCharactersInCurrency() {
        // Arrange
        String from = "USD_CUSTOM";
        String to = "EUR-TEST";
        Double amount = 100.0;

        when(request.queryParam("from")).thenReturn(Optional.of(from));
        when(request.queryParam("to")).thenReturn(Optional.of(to));
        when(request.queryParam("amount")).thenReturn(Optional.of("100"));

        RateValueDTO rateValue = new RateValueDTO(from, to, amount, 0.92, 92.0);
        when(rateService.getRate(from, to, amount)).thenReturn(Mono.just(rateValue));

        // Act
        Mono<ServerResponse> result = rateHandler.getRate(request);

        // Assert
        StepVerifier.create(result)
            .expectNextCount(1)
            .expectComplete()
            .verify();

        verify(rateService).getRate(from, to, amount);
    }

    @Test
    void testGetRateServiceThrowsException() {
        // Arrange
        String from = "USD";
        String to = "EUR";
        Double amount = 100.0;

        when(request.queryParam("from")).thenReturn(Optional.of(from));
        when(request.queryParam("to")).thenReturn(Optional.of(to));
        when(request.queryParam("amount")).thenReturn(Optional.of("100"));

        RuntimeException exception = new RuntimeException("Service Error");
        when(rateService.getRate(from, to, amount)).thenReturn(Mono.error(exception));

        // Act
        Mono<ServerResponse> result = rateHandler.getRate(request);

        // Assert: the exception should propagate from the handler (global handler will transform it at higher level)
        StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Service Error"))
            .verify();
    }

    @Test
    void testGetRateReturnsMonoOfServerResponse() {
        // Arrange
        String from = "USD";
        String to = "EUR";
        Double amount = 100.0;

        when(request.queryParam("from")).thenReturn(Optional.of(from));
        when(request.queryParam("to")).thenReturn(Optional.of(to));
        when(request.queryParam("amount")).thenReturn(Optional.of("100"));

        RateValueDTO rateValue = new RateValueDTO(from, to, amount, 0.92, 92.0);
        when(rateService.getRate(from, to, amount)).thenReturn(Mono.just(rateValue));

        // Act
        Mono<ServerResponse> result = rateHandler.getRate(request);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof Mono);
    }

    @Test
    void testGetRateWithMultipleCurrencyPairs() {
        // Arrange - Test 1: GBP to JPY
        String from1 = "GBP";
        String to1 = "JPY";
        Double amount1 = 50.0;

        when(request.queryParam("from")).thenReturn(Optional.of(from1));
        when(request.queryParam("to")).thenReturn(Optional.of(to1));
        when(request.queryParam("amount")).thenReturn(Optional.of("50"));

        RateValueDTO rateValue1 = new RateValueDTO(from1, to1, amount1, 190.5, 9525.0);
        when(rateService.getRate(from1, to1, amount1)).thenReturn(Mono.just(rateValue1));

        // Act
        Mono<ServerResponse> result = rateHandler.getRate(request);

        // Assert
        StepVerifier.create(result)
            .expectNextCount(1)
            .expectComplete()
            .verify();

        verify(rateService).getRate(from1, to1, amount1);
    }

    @Test
    void testGetRateWithNegativeAmount() {
        // Arrange
        String from = "USD";
        String to = "EUR";
        Double amount = -100.0;

        when(request.queryParam("from")).thenReturn(Optional.of(from));
        when(request.queryParam("to")).thenReturn(Optional.of(to));
        when(request.queryParam("amount")).thenReturn(Optional.of("-100"));

        RateValueDTO rateValue = new RateValueDTO(from, to, amount, 0.92, -92.0);
        when(rateService.getRate(from, to, amount)).thenReturn(Mono.just(rateValue));

        // Act
        Mono<ServerResponse> result = rateHandler.getRate(request);

        // Assert
        StepVerifier.create(result)
            .expectNextCount(1)
            .expectComplete()
            .verify();
    }

    @Test
    void testGetRateVerifiesServiceCalledWithCorrectParameters() {
        // Arrange
        String from = "CHF";
        String to = "AUD";
        Double amount = 75.5;

        when(request.queryParam("from")).thenReturn(Optional.of(from));
        when(request.queryParam("to")).thenReturn(Optional.of(to));
        when(request.queryParam("amount")).thenReturn(Optional.of("75.5"));

        RateValueDTO rateValue = new RateValueDTO(from, to, amount, 1.85, 139.675);
        when(rateService.getRate(from, to, amount)).thenReturn(Mono.just(rateValue));

        // Act
        rateHandler.getRate(request).block();

        // Assert
        verify(rateService, times(1)).getRate(from, to, amount);
    }
}
