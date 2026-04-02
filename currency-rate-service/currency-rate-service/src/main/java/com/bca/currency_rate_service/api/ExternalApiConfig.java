package com.bca.currency_rate_service.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ExternalApiConfig {

    public static final String BASE_URL_PUBLIC = "https://api.apilayer.com";
    public static final String BASE_URL = "http://localhost:3000";
    public static final String API_KEY = "OErqAKvh8bLyI0rEJtKp8EyFwNcAHgYk";

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(BASE_URL_PUBLIC)
                .defaultHeader("apiKey", API_KEY)  // Configura el encabezado con la API Key
                .build();
    }
}