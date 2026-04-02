package com.bca.conversion_business_service.dto.cotizacion;

import com.bca.conversion_business_service.dto.ExchangeRateRequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionResponseDTO {
	private String token;
	private ExchangeRateRequestDTO exchangeRate;
}
