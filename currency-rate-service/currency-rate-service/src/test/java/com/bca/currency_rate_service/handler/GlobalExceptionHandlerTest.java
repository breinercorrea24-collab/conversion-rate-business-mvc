package com.bca.currency_rate_service.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.test.StepVerifier;
import java.nio.file.AccessDeniedException;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.util.ReflectionTestUtils;

class GlobalExceptionHandlerTest {

    private org.springframework.mock.web.server.MockServerWebExchange mockExchange(String path) {
        org.springframework.mock.http.server.reactive.MockServerHttpRequest request = org.springframework.mock.http.server.reactive.MockServerHttpRequest.get(path).build();
        return org.springframework.mock.web.server.MockServerWebExchange.from(request);
    }

    @Test
    void illegalArgumentMapsTo400AndReturnsJson() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        org.springframework.mock.web.server.MockServerWebExchange exchange = mockExchange("/illegal");

        handler.handle(exchange, new IllegalArgumentException("bad input")).block();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
    }

    @Test
    void securityExceptionMapsTo401() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        org.springframework.mock.web.server.MockServerWebExchange exchange = mockExchange("/auth");

        handler.handle(exchange, new SecurityException("no perms")).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
    }

    @Test
    void accessDeniedMapsTo403() throws Exception {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        org.springframework.mock.web.server.MockServerWebExchange exchange = mockExchange("/forbidden");

        handler.handle(exchange, new AccessDeniedException("/restricted")).block();

        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
    }

    @Test
    void responseStatusExceptionUsesContainedStatus() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        org.springframework.mock.web.server.MockServerWebExchange exchange = mockExchange("/resp");

        handler.handle(exchange, new ResponseStatusException(HttpStatus.CONFLICT, "conflict")).block();

        assertEquals(HttpStatus.CONFLICT, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
    }

    @Test
    void runtimeExceptionMapsTo500AndContainsMessage() throws Exception {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        org.springframework.mock.web.server.MockServerWebExchange exchange = mockExchange("/runtime");

        handler.handle(exchange, new RuntimeException("boom")).block();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());

        String body = exchange.getResponse().getBodyAsString().block();
        System.out.println("DEBUG-BODY:" + body);
        assertNotNull(body);
        assertTrue(body.contains("boom") || body.contains("Error Controlado"), "body was: " + body);
        assertTrue(body.contains("Internal Server Error"));
    }

    @Test
    void serializerFailureFallsBackToSimpleJson() throws Exception {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        org.springframework.mock.web.server.MockServerWebExchange exchange = mockExchange("/runtime2");

        // Replace mapper so it throws when serializing
        ObjectMapper failing = new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JsonProcessingException {
                throw new JsonProcessingException("fail") { };
            }
        };

        ReflectionTestUtils.setField(handler, "mapper", failing);

        handler.handle(exchange, new RuntimeException("boom")).block();

        String body2 = exchange.getResponse().getBodyAsString().block();
        assertNotNull(body2);
        assertTrue(body2.contains("\"error\":\"Serialization error\""));
    }

    @Test
    void whenResponseAlreadyCommitted_returnsMonoError() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);

        when(exchange.getResponse()).thenReturn(response);
        when(response.isCommitted()).thenReturn(true);

        RuntimeException ex = new RuntimeException("already-committed");

        StepVerifier.create(handler.handle(exchange, ex))
            .expectErrorMatches(t -> t instanceof RuntimeException && t.getMessage().equals("already-committed"))
            .verify();
    }
}
