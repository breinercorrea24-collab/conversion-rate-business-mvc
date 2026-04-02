package com.bca.conversion_business_service.api;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bca.conversion_business_service.api.dto.ExchangeRateDTO;

import reactor.core.publisher.Mono;

@Service
public class CurrencyRateClient {

    private final WebClient webClient;

    public CurrencyRateClient(@Qualifier("currencyRateWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    // api/v1/rates?to=EUR&from=PEN&amount=100.1
    public Mono<ExchangeRateDTO> getRate(String to, String from, Double amount) {
        
        Supplier<ExchangeRateDTO> defaultRateSupplier =
            () -> new ExchangeRateDTO( from, to, amount, 
                1.0, amount * 1.0, "No se pudo obtener el tipo de cambio. Servicio no disponible.");

        return webClient.get()
                .uri(uri -> uri.path("/api/v1/rates")
                        .queryParam("to", to)
                        .queryParam("from", from)
                        .queryParam("amount", amount)
                        .build())
                .retrieve()
                .bodyToMono(ExchangeRateDTO.class)
                .onErrorResume(error -> {
                    System.out.println("Error llamando al servicio externo: " + error.getMessage());
                    return Mono.fromSupplier(defaultRateSupplier);
                });
    }
}