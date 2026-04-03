package com.bca.currency_rate_service.api;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bca.currency_rate_service.api.dto.ExchangeWebRateResponseDTO;

import reactor.core.publisher.Mono;

@Service
public class ExchangeRateClient {

    private final WebClient webClient;

    public ExchangeRateClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ExchangeWebRateResponseDTO> obtenerTasaDeCambio(String base, String symbols) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("fixer/latest")
                        .queryParam("base", base)
                        .queryParam("symbols", symbols)
                        .build())
                .retrieve()
                .bodyToMono(ExchangeWebRateResponseDTO.class);
    }
}