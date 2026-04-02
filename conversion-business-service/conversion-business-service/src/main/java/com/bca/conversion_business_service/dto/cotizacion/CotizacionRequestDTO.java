package com.bca.conversion_business_service.dto.cotizacion;

import com.bca.conversion_business_service.dto.ExchangeRateRequestDTO;
import com.bca.conversion_business_service.dto.OperationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionRequestDTO {
	private String status;
	private ExchangeRateRequestDTO preferenceRate;
	private OperationDTO operation;
}
