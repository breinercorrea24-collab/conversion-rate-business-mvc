package com.bca.conversion_exchange_rate.service;

import com.bca.conversion_exchange_rate.client.ExternalApiClient;
import com.bca.external.dto.CurrencyRateResponseDTO;
import com.bca.local.dto.SimulacionRequestDTO;
import com.bca.local.dto.SimulacionDTO;
import com.bca.local.dto.CotizacionRequestDTO;
import com.bca.local.dto.CotizacionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class OrchestratorService {

    private static final Logger logger = LoggerFactory.getLogger(OrchestratorService.class);
    private final ExternalApiClient client;
    private final ObjectMapper objectMapper;

    public OrchestratorService(ExternalApiClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public Mono<String> simulate(SimulacionRequestDTO request) {
        return simular(request)
                .map(dto -> {
                    try {
                        return objectMapper.writeValueAsString(dto);
                    } catch (Exception e) {
                        logger.error("Error serializando SimulacionDTO", e);
                        return "{}";
                    }
                });
    }

    public Mono<String> quote(CotizacionRequestDTO request) {
        return cotizar(request)
                .map(dto -> {
                    try {
                        return objectMapper.writeValueAsString(dto);
                    } catch (Exception e) {
                        logger.error("Error serializando CotizacionDTO", e);
                        return "{}";
                    }
                });
    }

    public Mono<SimulacionDTO> simular(SimulacionRequestDTO request) {
        logger.info("Simulando conversión de moneda");
        String from = request.getExchangeRate().getCurrency();
        String to = request.getExchangeRate().getTargetCurrency();
        Double amount = request.getExchangeRate().getAmount().doubleValue();

        logger.info("Obteniendo tasa de cambio: {} {} -> {}", amount, from, to);
        return client.getRate(from, to, amount)
                .map(response -> {
                    logger.info("Respuesta recibida: {}", response);
                    return mapToSimulacion(response, request);
                });
    }

    public Mono<CotizacionDTO> cotizar(CotizacionRequestDTO request) {
        logger.info("Generando cotización");
        return Mono.just(new CotizacionDTO());
    }

    private SimulacionDTO mapToSimulacion(CurrencyRateResponseDTO response,
            SimulacionRequestDTO request) {

        SimulacionDTO dto = new SimulacionDTO();

        dto.setAmount(response.getAmount());
        dto.setCurrency(response.getFrom());
        dto.setTargetCurrency(response.getTo());
        dto.setRate(response.getRate());
        dto.setRatePreferente(response.getRate() - 0.01); // lógica de negocio
        dto.setExchangeAmount(response.getConverted()); // monto ya convertido por el API
        dto.setDate(LocalDate.now());

        return dto;
    }

}