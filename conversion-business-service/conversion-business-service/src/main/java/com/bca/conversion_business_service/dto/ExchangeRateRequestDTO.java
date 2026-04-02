package com.bca.conversion_business_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateRequestDTO {
	
	private Long idSimulacion;
	private BigDecimal amount;
    private String currency;
    private String targetCurrency;
    
    private Map<String, BigDecimal> rates;
    
    private long timestamp;
    private LocalDate date;
    
    private String token;
}