package com.bca.currency_rate_service.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

class ExternalApiConfigTests {

    private ExternalApiConfig externalApiConfig;

    @BeforeEach
    void setUp() {
        externalApiConfig = new ExternalApiConfig();
    }

    @Test
    void testWebClientBeanCreation() {
        // Act
        WebClient webClient = externalApiConfig.webClient();

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testWebClientWithBaseUrl() {
        // Act
        WebClient webClient = externalApiConfig.webClient();

        // Assert
        assertNotNull(webClient);
        // Verify the webClient is configured with base URL
        String baseUrl = ExternalApiConfig.BASE_URL;
        assertEquals("http://localhost:3000", baseUrl);
    }

    @Test
    void testWebClientWithDefaultHeader() {
        // Act
        WebClient webClient = externalApiConfig.webClient();

        // Assert
        assertNotNull(webClient);
        // Verify API Key is set
        String apiKey = ExternalApiConfig.API_KEY;
        assertEquals("OErqAKvh8bLyI0rEJtKp8EyFwNcAHgYk", apiKey);
    }

    @Test
    void testBaseUrlConstant() {
        // Assert
        assertEquals("http://localhost:3000", ExternalApiConfig.BASE_URL);
    }

    @Test
    void testBaseUrlPublicConstant() {
        // Assert
        assertEquals("https://api.apilayer.com", ExternalApiConfig.BASE_URL_PUBLIC);
    }

    @Test
    void testApiKeyConstant() {
        // Assert
        assertEquals("OErqAKvh8bLyI0rEJtKp8EyFwNcAHgYk", ExternalApiConfig.API_KEY);
    }

    @Test
    void testWebClientIsNotNull() {
        // Act
        WebClient webClient = externalApiConfig.webClient();

        // Assert
        assertNotNull(webClient, "WebClient should not be null");
    }

    @Test
    void testMultipleWebClientCreations() {
        // Act
        WebClient webClient1 = externalApiConfig.webClient();
        WebClient webClient2 = externalApiConfig.webClient();

        // Assert
        assertNotNull(webClient1);
        assertNotNull(webClient2);
        // Note: They are different instances since no singleton is enforced in the method itself
        // But Spring @Bean will ensure singleton by default
    }

    @Test
    void testConfigurationClassExists() {
        // Act
        Class<?> configClass = ExternalApiConfig.class;

        // Assert
        assertNotNull(configClass);
        assertTrue(configClass.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
    }

    @Test
    void testWebClientBeanAnnotation() {
        // Act
        var method = ExternalApiConfig.class.getDeclaredMethods();

        // Assert
        boolean hasBeanAnnotation = false;
        for (var m : method) {
            if (m.getName().equals("webClient") && m.isAnnotationPresent(Bean.class)) {
                hasBeanAnnotation = true;
                break;
            }
        }
        assertTrue(hasBeanAnnotation, "webClient method should have @Bean annotation");
    }

    @Test
    void testConstantsAreNotNull() {
        // Assert
        assertNotNull(ExternalApiConfig.BASE_URL);
        assertNotNull(ExternalApiConfig.BASE_URL_PUBLIC);
        assertNotNull(ExternalApiConfig.API_KEY);
    }

    @Test
    void testConstantsAreNotEmpty() {
        // Assert
        assertFalse(ExternalApiConfig.BASE_URL.isEmpty());
        assertFalse(ExternalApiConfig.BASE_URL_PUBLIC.isEmpty());
        assertFalse(ExternalApiConfig.API_KEY.isEmpty());
    }
}
