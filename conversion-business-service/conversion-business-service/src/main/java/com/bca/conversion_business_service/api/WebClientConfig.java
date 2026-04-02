package com.bca.conversion_business_service.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient currencyRateWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081") // currency-rate-service
                .build();
    }
}