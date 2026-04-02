package com.bca.currency_rate_service.handler;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
@Order(-2) // Muy importante: se ejecuta antes de DefaultErrorWebExceptionHandler
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    // Register Java time module so Instant serializes correctly (avoid falling back to the simple JSON)
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status = mapToStatus(ex);

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        GlobalErrorResponse body = new GlobalErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        byte[] bytes;
        try {
            bytes = mapper.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = "{\"error\":\"Serialization error\"}".getBytes(StandardCharsets.UTF_8);
        }

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    private HttpStatus mapToStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException e) {
            return HttpStatus.valueOf(e.getStatusCode().value());
        }
        if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (ex instanceof SecurityException) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (ex instanceof java.nio.file.AccessDeniedException) {
            return HttpStatus.FORBIDDEN;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}