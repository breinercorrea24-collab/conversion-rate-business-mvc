package com.bca.conversion_business_service.controller.mapper;

import java.time.LocalDate;

import com.bca.conversion_business_service.api.dto.ExchangeRateDTO;
import com.bca.conversion_business_service.dto.ClienteDTO;
import com.bca.conversion_business_service.dto.CotizacionDTO;
import com.bca.conversion_business_service.dto.SimulacionDTO;
import com.bca.conversion_business_service.dto.cotizacion.CotizacionRequestDTO;

public class Mapper {

	private Mapper() { }

	public static SimulacionDTO simulate(ClienteDTO clienteDTO, ExchangeRateDTO exchangeRateResponseDTO) {

        String from = exchangeRateResponseDTO.getFrom();
        String to = exchangeRateResponseDTO.getTo();    
        Double amount = exchangeRateResponseDTO.getAmount().doubleValue();      
		Double rate = exchangeRateResponseDTO.getRate();
        Double converted = exchangeRateResponseDTO.getConverted();
		Double tasaPreferencial = clienteDTO.getTasaPreferencial();

		
		SimulacionDTO simulacionDTO = new SimulacionDTO();
		simulacionDTO.setIdCliente(clienteDTO.getIdCliente());

		simulacionDTO.setCurrency(from);
		simulacionDTO.setTargetCurrency(to);

		simulacionDTO.setAmount(amount);
		
		simulacionDTO.setRate(rate);

		rate = getRate(rate, tasaPreferencial);
		simulacionDTO.setRatePreferente(rate);
		
		simulacionDTO.setExchangeAmount(converted);

		simulacionDTO.setDate(LocalDate.now());

		return simulacionDTO;
	}
	
	public static Double getRate(Double rate, Double tasaPreferencial) {
		return rate - tasaPreferencial;
	}

	public static CotizacionDTO toCotizacionDTO(CotizacionRequestDTO request) {
		CotizacionDTO cotizacionDTO = new CotizacionDTO();
		cotizacionDTO.setIdSimulacion(request.getPreferenceRate().getIdSimulacion());
		cotizacionDTO.setAmount(request.getPreferenceRate().getAmount());
		cotizacionDTO.setCurrency(request.getPreferenceRate().getCurrency());
		cotizacionDTO.setTargetCurrency(request.getOperation().getExchangeRate().getTargetCurrency());
		cotizacionDTO.setStatus(request.getStatus());
		cotizacionDTO.setDate(LocalDate.now());
		return cotizacionDTO;
	}
}
