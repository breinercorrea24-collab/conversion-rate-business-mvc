package com.bca.conversion_business_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponseDTO {

	private BigDecimal amount;
    private String currency;
    private String targetCurrency;
    
    private Map<String, BigDecimal> rates;
    
    private long timestamp;
    private LocalDate date;
}
