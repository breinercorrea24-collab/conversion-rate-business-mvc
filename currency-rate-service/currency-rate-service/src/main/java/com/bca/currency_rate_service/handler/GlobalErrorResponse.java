package com.bca.currency_rate_service.handler;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "Respuesta de error")
public class GlobalErrorResponse {
    @Schema(description = "Timestamp del error", example = "2024-01-01T12:00:00Z")
    private final Instant timestamp;
    @Schema(description = "Código HTTP del error", example = "400")
    private final int status;
    @Schema(description = "Tipo de error", example = "Bad Request")
    private final String error;
    @Schema(description = "Mensaje descriptivo del error", example = "Currency parameters are required")
    private final String message;
    @Schema(description = "Path del endpoint", example = "/api/v1/rates")
    private final String path;

    public GlobalErrorResponse(Instant timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
}
