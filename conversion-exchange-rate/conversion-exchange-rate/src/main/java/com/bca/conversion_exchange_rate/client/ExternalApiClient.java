package com.bca.conversion_exchange_rate.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.bca.external.dto.CurrencyRateResponseDTO;

import reactor.core.publisher.Mono;

@Component
public class ExternalApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiClient.class);
    private final WebClient webClient;

    public ExternalApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<CurrencyRateResponseDTO> getRate(String from, String to, Double amount) {
        String url = String.format("http://localhost:8081/api/v1/rates?to=%s&from=%s&amount=%s", to, from, amount);
        logger.info("Llamando a API externo: {}", url);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/rates")
                        .queryParam("to", to)
                        .queryParam("from", from)
                        .queryParam("amount", amount)
                        .build())
                .retrieve()
                .onStatus(status -> status.is5xxServerError(), 
                    response -> response.bodyToMono(String.class)
                        .flatMap(body -> {
                            logger.error("Error 5xx del servidor remoto: {}", body);
                            return Mono.error(new RuntimeException("Error del servidor remoto: " + body));
                        }))
                .onStatus(status -> status.is4xxClientError(),
                    response -> response.bodyToMono(String.class)
                        .flatMap(body -> {
                            logger.error("Error 4xx - Solicitud inválida: {}", body);
                            return Mono.error(new RuntimeException("Solicitud inválida: " + body));
                        }))
                .bodyToMono(CurrencyRateResponseDTO.class)
                .doOnError(error -> logger.error("Error llamando a API externo", error));
    }
}