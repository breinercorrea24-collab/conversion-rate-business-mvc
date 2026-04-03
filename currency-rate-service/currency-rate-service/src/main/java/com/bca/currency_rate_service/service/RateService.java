package com.bca.currency_rate_service.service;

import org.springframework.stereotype.Service;

import com.bca.currency_rate_service.api.ExchangeRateClient;
import com.bca.currency_rate_service.api.dto.CurrencyRateResponseDTO;

import reactor.core.publisher.Mono;

@Service
public class RateService {

    private final ExchangeRateClient exchangeRateClient;

    public RateService(ExchangeRateClient exchangeRateClient) {
        this.exchangeRateClient = exchangeRateClient;
    }

    public Mono<CurrencyRateResponseDTO> getRate(String from, String to, Double amount) {
        return exchangeRateClient.obtenerTasaDeCambio(from, to)
                .map(response -> {
                    double rate = response.getRates()
                            .getOrDefault(to, 1.0);
                    double convertedAmount = rate * amount;

                    return new CurrencyRateResponseDTO(from, to, amount, rate, convertedAmount);
                });
    }
}