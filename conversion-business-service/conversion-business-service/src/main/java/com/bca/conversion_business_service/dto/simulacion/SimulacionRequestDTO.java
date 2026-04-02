package com.bca.conversion_business_service.dto.simulacion;


import com.bca.conversion_business_service.dto.ClienteDTO;
import com.bca.conversion_business_service.dto.ExchangeRateRequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacionRequestDTO {
	private ClienteDTO datosCliente;
    private ExchangeRateRequestDTO exchangeRate;
}
