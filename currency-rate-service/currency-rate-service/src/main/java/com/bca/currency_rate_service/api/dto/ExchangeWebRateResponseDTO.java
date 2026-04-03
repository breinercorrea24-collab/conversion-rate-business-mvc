package com.bca.currency_rate_service.api.dto;

import java.time.LocalDate;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExchangeRateResponse", description = "Respuesta con la tasa de cambio")
public class ExchangeWebRateResponseDTO {

	@Schema(description = "Monto a convertir", example = "100.0")
	private Double amount;
    @Schema(description = "Moneda origen", example = "USD")
	private String sourceCurrency;
    @Schema(description = "Moneda destino", example = "EUR")
	private String targetCurrency;
    
    @Schema(description = "Tasas de cambio")
	private Map<String, Double> rates;
    
    @Schema(description = "Timestamp en milisegundos", example = "1704067200000")
	private long timestamp;
    @Schema(description = "Fecha de la tasa", example = "2024-01-01")
	private LocalDate date;
}
