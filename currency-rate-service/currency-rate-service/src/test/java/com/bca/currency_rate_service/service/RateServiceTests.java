package com.bca.currency_rate_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bca.currency_rate_service.api.ExchangeRateClient;
import com.bca.currency_rate_service.api.dto.ExchangeWebRateResponseDTO;
import com.bca.currency_rate_service.api.dto.CurrencyRateResponseDTO;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RateServiceTests {

    @Mock
    private ExchangeRateClient exchangeRateClient;

    private RateService rateService;

    @BeforeEach
    void setUp() {
        rateService = new RateService(exchangeRateClient);
    }

    @Test
    void testConstructorInitializesExchangeRateClient() {
        // Act & Assert
        assertNotNull(rateService);
    }

    @Test
    void testGetRateSuccessful() {
        // Arrange
        String from = "USD";
        String to = "EUR";
        Double amount = 100.0;

        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.92);

        ExchangeWebRateResponseDTO response = new ExchangeWebRateResponseDTO(
                100.0, "USD", "EUR", rates, System.currentTimeMillis(), LocalDate.now());

        doReturn(Mono.just(response)).when(exchangeRateClient).obtenerTasaDeCambio(from, to);

        // Act
        Mono<CurrencyRateResponseDTO> result = rateService.getRate(from, to, amount);

        // Assert
        StepVerifier.create(result)
                .assertNext(rateValue -> {
                    assertEquals("USD", rateValue.getFrom());
                    assertEquals("EUR", rateValue.getTo());
                    assertEquals(100.0, rateValue.getAmount());
                    assertEquals(0.92, rateValue.getRate());
                    assertEquals(92.0, rateValue.getConverted());
                })
                .verifyComplete();

        verify(exchangeRateClient).obtenerTasaDeCambio(from, to);
    }

    @Test
    void testGetRateWithDefaultRate() {
        // Arrange
        String from = "USD";
        String to = "XXX";
        Double amount = 50.0;

        Map<String, Double> rates = new HashMap<>();
        // XXX not in rates, should default to 1.0

        ExchangeWebRateResponseDTO response = new ExchangeWebRateResponseDTO(
                50.0, "USD", "XXX", rates, System.currentTimeMillis(), LocalDate.now());

        doReturn(Mono.just(response)).when(exchangeRateClient).obtenerTasaDeCambio(from, to);

        // Act
        Mono<CurrencyRateResponseDTO> result = rateService.getRate(from, to, amount);

        // Assert
        StepVerifier.create(result)
                .assertNext(rateValue -> {
                    assertEquals("USD", rateValue.getFrom());
                    assertEquals("XXX", rateValue.getTo());
                    assertEquals(50.0, rateValue.getAmount());
                    assertEquals(1.0, rateValue.getRate());
                    assertEquals(50.0, rateValue.getConverted());
                })
                .verifyComplete();
    }

    @Test
    void testGetRateWithZeroAmount() {
        // Arrange
        String from = "GBP";
        String to = "JPY";
        Double amount = 0.0;

        Map<String, Double> rates = new HashMap<>();
        rates.put("JPY", 190.5);

        ExchangeWebRateResponseDTO response = new ExchangeWebRateResponseDTO(
                0.0, "GBP", "JPY", rates, System.currentTimeMillis(), LocalDate.now());

        doReturn(Mono.just(response)).when(exchangeRateClient).obtenerTasaDeCambio(from, to);

        // Act
        Mono<CurrencyRateResponseDTO> result = rateService.getRate(from, to, amount);

        // Assert
        StepVerifier.create(result)
                .assertNext(rateValue -> {
                    assertEquals(0.0, rateValue.getAmount());
                    assertEquals(0.0, rateValue.getConverted());
                })
                .verifyComplete();
    }

    @Test
    void testGetRateWithLargeAmount() {
        // Arrange
        String from = "USD";
        String to = "EUR";
        Double amount = 1000000.0;

        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.92);

        ExchangeWebRateResponseDTO response = new ExchangeWebRateResponseDTO(
                1000000.0, "USD", "EUR", rates, System.currentTimeMillis(), LocalDate.now());

        doReturn(Mono.just(response)).when(exchangeRateClient).obtenerTasaDeCambio(from, to);

        // Act
        Mono<CurrencyRateResponseDTO> result = rateService.getRate(from, to, amount);

        // Assert
        StepVerifier.create(result)
                .assertNext(rateValue -> {
                    assertEquals(1000000.0, rateValue.getAmount());
                    assertEquals(920000.0, rateValue.getConverted());
                })
                .verifyComplete();
    }

    @Test
    void testGetRateWithSmallAmount() {
        // Arrange
        String from = "EUR";
        String to = "USD";
        Double amount = 0.01;

        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.09);

        ExchangeWebRateResponseDTO response = new ExchangeWebRateResponseDTO(
                0.01, "EUR", "USD", rates, System.currentTimeMillis(), LocalDate.now());

        doReturn(Mono.just(response)).when(exchangeRateClient).obtenerTasaDeCambio(from, to);

        // Act
        Mono<CurrencyRateResponseDTO> result = rateService.getRate(from, to, amount);

        // Assert
        StepVerifier.create(result)
                .assertNext(rateValue -> {
                    assertEquals(0.01, rateValue.getAmount());
                    assertEquals(0.0109, rateValue.getConverted(), 0.0001);
                })
                .verifyComplete();
    }

    @Test
    void testGetRateWithMultipleCurrenciesInResponse() {
        // Arrange
        String from = "USD";
        String to = "GBP";
        Double amount = 100.0;

        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.92);
        rates.put("GBP", 0.79);
        rates.put("JPY", 150.5);
        rates.put("CAD", 1.36);

        ExchangeWebRateResponseDTO response = new ExchangeWebRateResponseDTO(
                100.0, "USD", "GBP", rates, System.currentTimeMillis(), LocalDate.now());

        doReturn(Mono.just(response)).when(exchangeRateClient).obtenerTasaDeCambio(from, to);

        // Act
        Mono<CurrencyRateResponseDTO> result = rateService.getRate(from, to, amount);

        // Assert
        StepVerifier.create(result)
                .assertNext(rateValue -> {
                    assertEquals("GBP", rateValue.getTo());
                    assertEquals(0.79, rateValue.getRate());
                    assertEquals(79.0, rateValue.getConverted());
                })
                .verifyComplete();
    }

    @Test
    void testGetRateThrowsException() {
        // Arrange
        String from = "USD";
        String to = "EUR";
        Double amount = 100.0;

        RuntimeException exception = new RuntimeException("API Error");
        doReturn(Mono.error(exception)).when(exchangeRateClient).obtenerTasaDeCambio(from, to);

        // Act
        Mono<CurrencyRateResponseDTO> result = rateService.getRate(from, to, amount);

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testGetRateWithHighRate() {
        // Arrange
        String from = "USD";
        String to = "VEF";
        Double amount = 100.0;

        Map<String, Double> rates = new HashMap<>();
        rates.put("VEF", 2550000.0); // Very high exchange rate

        ExchangeWebRateResponseDTO response = new ExchangeWebRateResponseDTO(
                100.0, "USD", "VEF", rates, System.currentTimeMillis(), LocalDate.now());

        doReturn(Mono.just(response)).when(exchangeRateClient).obtenerTasaDeCambio(from, to);

        // Act
        Mono<CurrencyRateResponseDTO> result = rateService.getRate(from, to, amount);

        // Assert
        StepVerifier.create(result)
                .assertNext(rateValue -> {
                    assertEquals(2550000.0, rateValue.getRate());
                    assertEquals(255000000.0, rateValue.getConverted());
                })
                .verifyComplete();
    }

    @Test
    void testGetRateWithVeryLowRate() {
        // Arrange
        String from = "USD";
        String to = "BTC";
        Double amount = 1000.0;

        Map<String, Double> rates = new HashMap<>();
        rates.put("BTC", 0.000023); // Very low exchange rate

        ExchangeWebRateResponseDTO response = new ExchangeWebRateResponseDTO(
                1000.0, "USD", "BTC", rates, System.currentTimeMillis(), LocalDate.now());

        doReturn(Mono.just(response)).when(exchangeRateClient).obtenerTasaDeCambio(from, to);

        // Act
        Mono<CurrencyRateResponseDTO> result = rateService.getRate(from, to, amount);

        // Assert
        StepVerifier.create(result)
                .assertNext(rateValue -> {
                    assertEquals(0.000023, rateValue.getRate());
                    assertEquals(0.023, rateValue.getConverted(), 0.0001);
                })
                .verifyComplete();
    }

    @Test
    void testGetRateReturnsMonoType() {
        // Arrange
        String from = "USD";
        String to = "EUR";
        Double amount = 100.0;

        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.92);

        ExchangeWebRateResponseDTO response = new ExchangeWebRateResponseDTO(
                100.0, "USD", "EUR", rates, System.currentTimeMillis(), LocalDate.now());

        doReturn(Mono.just(response)).when(exchangeRateClient).obtenerTasaDeCambio(from, to);

        // Act
        Mono<CurrencyRateResponseDTO> result = rateService.getRate(from, to, amount);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof Mono);
    }

    @Test
    void testGetRateVerifiesClientCall() {
        // Arrange
        String from = "CHF";
        String to = "AUD";
        Double amount = 50.0;

        Map<String, Double> rates = new HashMap<>();
        rates.put("AUD", 1.85);

        ExchangeWebRateResponseDTO response = new ExchangeWebRateResponseDTO(
                50.0, "CHF", "AUD", rates, System.currentTimeMillis(), LocalDate.now());

        doReturn(Mono.just(response)).when(exchangeRateClient).obtenerTasaDeCambio(from, to);

        // Act
        rateService.getRate(from, to, amount).block();

        // Assert
        verify(exchangeRateClient, times(1)).obtenerTasaDeCambio(from, to);
    }

    @Test
    void testGetRateWithDifferentCurrencyCombinations() {
        // Arrange
        String from = "JPY";
        String to = "INR";
        Double amount = 1000.0;

        Map<String, Double> rates = new HashMap<>();
        rates.put("INR", 0.53);

        ExchangeWebRateResponseDTO response = new ExchangeWebRateResponseDTO(
                1000.0, "JPY", "INR", rates, System.currentTimeMillis(), LocalDate.now());

        doReturn(Mono.just(response)).when(exchangeRateClient).obtenerTasaDeCambio(from, to);

        // Act
        Mono<CurrencyRateResponseDTO> result = rateService.getRate(from, to, amount);

        // Assert
        StepVerifier.create(result)
                .assertNext(rateValue -> {
                    assertEquals("JPY", rateValue.getFrom());
                    assertEquals("INR", rateValue.getTo());
                    assertEquals(1000.0, rateValue.getAmount());
                    assertEquals(0.53, rateValue.getRate());
                    assertEquals(530.0, rateValue.getConverted());
                })
                .verifyComplete();
    }
}
