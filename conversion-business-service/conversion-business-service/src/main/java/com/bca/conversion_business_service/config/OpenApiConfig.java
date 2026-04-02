package com.bca.conversion_business_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Conversion Business Service API")
                        .version("0.0.1")
                        .description("API reactiva para simulaciones y cotizaciones de cambio de divisas")
                        .contact(new Contact().name("BCA API Team")));
    }
}
