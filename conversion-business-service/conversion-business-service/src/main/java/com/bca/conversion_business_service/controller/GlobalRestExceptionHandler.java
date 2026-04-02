package com.bca.conversion_business_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalRestExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleBadRequest(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage())));
    }

    @ExceptionHandler(SecurityException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleUnauthorized(SecurityException ex) {
        log.warn("Unauthorized: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, ex.getMessage())));
    }

    /**
     * Catch ResponseStatusException so callers can raise exceptions with custom status (including 403).
     */
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleResponseStatus(ResponseStatusException ex) {

        HttpStatusCode status = ex.getStatusCode();
        HttpStatus httpStatus = HttpStatus.valueOf(status.value());

        log.warn("ResponseStatusException ({}): {}", httpStatus.value(), ex.getReason());

        return Mono.just(
                ResponseEntity.status(httpStatus)
                        .body(buildError(httpStatus, ex.getReason()))
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleServerError(RuntimeException ex) {
        log.error("Internal server error", ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage())));
    }

    private Map<String, Object> buildError(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message == null ? "" : message);
        body.put("timestamp", LocalDateTime.now().toString());
        return body;
    }
}
