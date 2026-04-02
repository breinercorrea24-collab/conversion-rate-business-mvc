package com.bca.conversion_business_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springdoc.core.customizers.OpenApiCustomizer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Configuration
public class OpenApiExamplesConfig {

    private static final Logger logger = Logger.getLogger(OpenApiExamplesConfig.class.getName());

    @Bean
    public OpenApiCustomizer examplesCustomiser(ResourceLoader resourceLoader) {
        return openApi -> {
            try {
                logger.info("Starting to load OpenAPI examples");
                
                // Cargar ejemplos de responses
                Object response_200_simulate = loadExample(resourceLoader, "simulate/simulacion-respuesta.json");
                Object response_400_simulate  = loadExample(resourceLoader, "simulate/error-400.json");
                Object response_503_simulate  = loadExample(resourceLoader, "simulate/error-503.json");

                Object response_200_quotation = loadExample(resourceLoader, "quotation/cotizacion-respuesta.json");
                Object response_400_quotation  = loadExample(resourceLoader, "quotation/error-400.json");
                Object response_503_quotation  = loadExample(resourceLoader, "quotation/error-503.json");

                // Cargar ejemplos de requests
                Object simulacion_request = loadExample(resourceLoader, "simulacion-request.json");
                Object cotizacion_request = loadExample(resourceLoader, "cotizacion-request.json");

                logger.info("Examples loaded successfully");

                // Aplicar ejemplos a exchange-rate
                applyExamplesToEndpoint(openApi, "/api/v1/exchange-rate", 
                    response_200_simulate, response_400_simulate, response_503_simulate, 
                    simulacion_request);

                // Aplicar ejemplos a booking-rate
                applyExamplesToEndpoint(openApi, "/api/v1/booking-rate", 
                    response_200_quotation, response_400_quotation, response_503_quotation, 
                    cotizacion_request);

                logger.info("Examples applied to endpoints successfully");

            } catch (Exception ex) {
                logger.severe("Failed to load OpenAPI examples: " + ex.getMessage());
                ex.printStackTrace();
                throw new RuntimeException("Failed to load OpenAPI examples", ex);
            }
        };
    }

    private void applyExamplesToEndpoint(OpenAPI openApi, String endpoint, Object response200, Object response400, Object response503, Object requestExample) {
        if (openApi.getPaths() == null || openApi.getPaths().get(endpoint) == null) {
            return;
        }

        // Intentar GET primero, luego POST (para soportar ambos tipos de endpoints)
        var pathItem = openApi.getPaths().get(endpoint);
        var operation = pathItem.getGet() != null ? pathItem.getGet() : pathItem.getPost();
        
        if (operation == null) {
            return;
        }

        // Aplicar ejemplos a responses
        if (operation.getResponses() != null) {
            applyExampleToResponse(operation, "200", response200);
            applyExampleToResponse(operation, "400", response400);
            applyExampleToResponse(operation, "503", response503);
        }

        // Aplicar ejemplo a request body solo si requestExample no es null
        if (requestExample != null && operation.getRequestBody() != null && operation.getRequestBody().getContent() != null) {
            MediaType mediaType = operation.getRequestBody().getContent().get("application/json");
            if (mediaType != null) {
                mediaType.setExample(requestExample);
            }
        }
    }

    private void applyExampleToResponse(io.swagger.v3.oas.models.Operation operation, String statusCode, Object example) {
        if (example == null) {
            return;
        }
        
        var response = operation.getResponses().get(statusCode);
        if (response != null && response.getContent() != null) {
            MediaType mediaType = response.getContent().get("application/json");
            if (mediaType != null) {
                mediaType.setExample(example);
            }
        }
    }

    public Object loadExample(ResourceLoader resourceLoader, String path) {
        String LOCATION = "classpath:static/examples/";
        path = LOCATION + path;
        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = new String(resourceLoader.getResource(path).getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);
            return mapper.readValue(json, Object.class);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load example from " + path, ex);
        }
    }
}