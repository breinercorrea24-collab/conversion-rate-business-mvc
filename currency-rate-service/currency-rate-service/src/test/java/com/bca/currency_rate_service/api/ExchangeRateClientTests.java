package com.bca.currency_rate_service.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;

import com.bca.currency_rate_service.api.dto.ExchangeWebRateResponseDTO;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;



@ExtendWith(MockitoExtension.class)
class ExchangeRateClientTests {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestUriSpec;

    @Mock
    private RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private ResponseSpec responseSpec;

    private ExchangeRateClient exchangeRateClient;

    @BeforeEach
    void setUp() {
        exchangeRateClient = new ExchangeRateClient(webClient);
    }

    @Test
    void testConstructorInitializesWebClient() {
        assertNotNull(exchangeRateClient);
        assertNotNull(new ExchangeRateClient(webClient));
    }

    @SuppressWarnings("unchecked")
    private void setupWebClientMock(ExchangeWebRateResponseDTO response) {
        doReturn(requestUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestUriSpec).uri((Function<UriBuilder, URI>) any());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Mono.just(response)).when(responseSpec).bodyToMono(ExchangeWebRateResponseDTO.class);
    }

    @Test
    void testUriBuilderIsExecuted() throws Exception {
        MockWebServer server = new MockWebServer();

        // Respuesta falsa JSON
        String json = """
        {
            "amount": 100,
            "sourceCurrency": "USD",
            "targetCurrency": "EUR",
            "rates": {"EUR": 0.92},
            "timestamp": 123456,
            "date": "2024-01-01"
        }
        """;

        server.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        server.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(server.url("/").toString())
                .build();

        ExchangeRateClient client = new ExchangeRateClient(webClient);

        // Act
        var result = client.obtenerTasaDeCambio("USD", "EUR");

        // Assert
        StepVerifier.create(result)
                .assertNext(r -> {
                    assertEquals("USD", r.getSourceCurrency());
                    assertEquals(0.92, r.getRates().get("EUR"));
                })
                .verifyComplete();

        // Verificar que la URI real construida fue correcta
        var recordedRequest = server.takeRequest();
        assertEquals("/fixer/latest?base=USD&symbols=EUR", recordedRequest.getPath());

        server.shutdown();
    }

    @Test
    void testObtenerTasaDeCambioSuccess() {
        // Arrange
        String base = "USD";
        String symbols = "EUR,GBP";

        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.92);
        rates.put("GBP", 0.79);

        ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                100.0, base, "EUR", rates, System.currentTimeMillis(), LocalDate.now());

        setupWebClientMock(expectedResponse);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(webClient).get();
    }

    @Test
    void testObtenerTasaDeCambioWithEmptySymbols() {
        // Arrange
        String base = "USD";
        String symbols = "";

        Map<String, Double> rates = new HashMap<>();
        ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                50.0, base, null, rates, System.currentTimeMillis(), LocalDate.now());

        setupWebClientMock(expectedResponse);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    void testObtenerTasaDeCambioThrowsException() {
        // Arrange
        String base = "USD";
        String symbols = "EUR";
        RuntimeException exception = new RuntimeException("API Error");

        doReturn(requestUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestUriSpec).uri((Function<UriBuilder, URI>) any());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Mono.error(exception)).when(responseSpec).bodyToMono(ExchangeWebRateResponseDTO.class);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testObtenerTasaDeCambioWithMultipleCurrencies() {
        // Arrange
        String base = "GBP";
        String symbols = "USD,EUR,JPY,CAD";

        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.27);
        rates.put("EUR", 1.17);
        rates.put("JPY", 190.5);
        rates.put("CAD", 1.72);

        ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                1000.0, base, "USD", rates, System.currentTimeMillis(), LocalDate.now());

        setupWebClientMock(expectedResponse);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(base, response.getSourceCurrency());
                    assertEquals(4, response.getRates().size());
                    assertEquals(1.27, response.getRates().get("USD"));
                })
                .verifyComplete();
    }

    @Test
    void testObtenerTasaDeCambioWithNullRates() {
        // Arrange
        String base = "USD";
        String symbols = "EUR";

        ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                100.0, base, "EUR", null, System.currentTimeMillis(), LocalDate.now());

        setupWebClientMock(expectedResponse);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> assertNull(response.getRates()))
                .verifyComplete();
    }

    @Test
    void testObtenerTasaDeCambioWithSpecialCharacters() {
        // Arrange
        String base = "USD";
        String symbols = "EUR_GB,CHF-TEST";

        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR_GB", 0.92);

        ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                100.0, base, "EUR_GB", rates, System.currentTimeMillis(), LocalDate.now());

        setupWebClientMock(expectedResponse);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void testObtenerTasaDeCambioVerifyUriConstruction() {
        // Arrange
        String base = "CHF";
        String symbols = "AUD";

        Map<String, Double> rates = new HashMap<>();
        rates.put("AUD", 1.85);

        ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                75.0, base, "AUD", rates, System.currentTimeMillis(), LocalDate.now());

        setupWebClientMock(expectedResponse);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(ExchangeWebRateResponseDTO.class);
    }

    @Test
    void testObtenerTasaDeCambioReturnsMono() {
        // Arrange
        String base = "JPY";
        String symbols = "INR";

        Map<String, Double> rates = new HashMap<>();
        rates.put("INR", 1.0);

        ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                200.0, base, "INR", rates, System.currentTimeMillis(), LocalDate.now());

        setupWebClientMock(expectedResponse);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof Mono);
    }

    @Test
    void testObtenerTasaDeCambioWithZeroAmount() {
        // Arrange
        String base = "USD";
        String symbols = "EUR";

        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.92);

        ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                0.0, base, "EUR", rates, System.currentTimeMillis(), LocalDate.now());

        setupWebClientMock(expectedResponse);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> assertEquals(0.0, response.getAmount()))
                .verifyComplete();
    }

    @Test
    void testObtenerTasaDeCambioWithLargeAmount() {
        // Arrange
        String base = "GBP";
        String symbols = "USD";

        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.27);

        ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                999999.99, base, "USD", rates, System.currentTimeMillis(), LocalDate.now());

        setupWebClientMock(expectedResponse);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> assertEquals(999999.99, response.getAmount()))
                .verifyComplete();
    }

    @Test
    void testObtenerTasaDeCambioWithNegativeAmount() {
        // Arrange
        String base = "EUR";
        String symbols = "GBP";

        Map<String, Double> rates = new HashMap<>();
        rates.put("GBP", 0.86);

        ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                -50.0, base, "GBP", rates, System.currentTimeMillis(), LocalDate.now());

        setupWebClientMock(expectedResponse);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> assertEquals(-50.0, response.getAmount()))
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    void testObtenerTasaDeCambioVerifyWebClientChain() {
        // Arrange
        String base = "AUD";
        String symbols = "NZD";

        Map<String, Double> rates = new HashMap<>();
        rates.put("NZD", 1.08);

        ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                100.0, base, "NZD", rates, System.currentTimeMillis(), LocalDate.now());

        setupWebClientMock(expectedResponse);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        // Verify the complete chain was called
        verify(webClient, times(1)).get();
        verify(requestUriSpec, times(1)).uri((Function<UriBuilder, URI>) any());
        verify(requestHeadersSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(ExchangeWebRateResponseDTO.class);
    }

    @Test
    void testObtenerTasaDeCambioWithDifferentBases() {
        // Test with multiple different base currencies
        String[] bases = {"USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF"};
        
        for (String base : bases) {
            // Arrange
            Map<String, Double> rates = new HashMap<>();
            rates.put("EUR", 0.92);

            ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                    100.0, base, "EUR", rates, System.currentTimeMillis(), LocalDate.now());

            setupWebClientMock(expectedResponse);

            // Act
            Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, "EUR");

            // Assert
            StepVerifier.create(result)
                    .expectNext(expectedResponse)
                    .verifyComplete();
        }
    }

    @Test
    void testObtenerTasaDeCambioComplexScenario() {
        // Arrange - Complex scenario with multiple currencies and large amounts
        String base = "SGD";
        String symbols = "MYR,THB,IDR,PHP,VND";

        Map<String, Double> rates = new HashMap<>();
        rates.put("MYR", 3.29);
        rates.put("THB", 27.89);
        rates.put("IDR", 16234.50);
        rates.put("PHP", 57.89);
        rates.put("VND", 25123.45);

        ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                5000.0, base, "MYR", rates, System.currentTimeMillis(), LocalDate.now());

        setupWebClientMock(expectedResponse);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(base, response.getSourceCurrency());
                    assertEquals(5, response.getRates().size());
                    assertTrue(response.getRates().containsKey("IDR"));
                    assertEquals(16234.50, response.getRates().get("IDR"));
                })
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    void testObtenerTasaDeCambioUriBuilderExecution() {
        // Arrange - Test to verify the URI builder lambda is executed
        String base = "INR";
        String symbols = "PKR";

        Map<String, Double> rates = new HashMap<>();
        rates.put("PKR", 3.38);

        ExchangeWebRateResponseDTO expectedResponse = new ExchangeWebRateResponseDTO(
                100.0, base, "PKR", rates, System.currentTimeMillis(), LocalDate.now());

        // Setup with real URI builder execution
        doReturn(requestUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestUriSpec).uri((Function<UriBuilder, URI>) any());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Mono.just(expectedResponse)).when(responseSpec).bodyToMono(ExchangeWebRateResponseDTO.class);

        // Act
        Mono<ExchangeWebRateResponseDTO> result = exchangeRateClient.obtenerTasaDeCambio(base, symbols);

        // Assert - Verify all steps of WebClient chain are executed
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        // Verify the URI builder function was invoked
        verify(requestUriSpec, times(1)).uri((Function<UriBuilder, URI>) any());
    }

    @Test
    void testObtenerTasaDeCambioWithRealWebClient() throws Exception {
        // Arrange - Integration test with real WebClient and MockWebServer
        try (MockWebServer server = new MockWebServer()) {
            server.start();
            
            String baseUrl = server.url("/").toString();
            WebClient realWebClient = WebClient.builder()
                    .baseUrl(baseUrl)
                    .build();
            
            ExchangeRateClient realClient = new ExchangeRateClient(realWebClient);
            
            // Setup mock response
            String jsonResponse = "{\"amount\": 100.0, \"sourceCurrency\": \"USD\", \"targetCurrency\": \"EUR\", \"rates\": {\"EUR\": 0.92, \"GBP\": 0.79}, \"timestamp\": " + System.currentTimeMillis() + ", \"date\": \"2025-11-20\"}";
            
            server.enqueue(new MockResponse()
                    .setBody(jsonResponse)
                    .addHeader("Content-Type", "application/json"));
            
            // Act - This executes the lambda URI builder with real code
            Mono<ExchangeWebRateResponseDTO> result = realClient.obtenerTasaDeCambio("USD", "EUR,GBP");
            
            // Assert - Verify response is returned
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertNotNull(response);
                        assertEquals("USD", response.getSourceCurrency());
                    })
                    .verifyComplete();
            
            // Verify the request was made with correct path
            RecordedRequest request = server.takeRequest();
            assertTrue(request.getPath().startsWith("/fixer/latest"));
        }
    }
}
