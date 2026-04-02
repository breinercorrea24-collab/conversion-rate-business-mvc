package com.bca.currency_rate_service.api.dto;

import java.time.LocalDate;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponseDTO {

	private Double amount;
    private String sourceCurrency;
    private String targetCurrency;
    
    private Map<String, Double> rates;
    
    private long timestamp;
    private LocalDate date;
}
