package com.bca.conversion_exchange_rate.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import com.bca.api.ApiApi;
import com.bca.local.dto.SimulacionRequestDTO;
import com.bca.local.dto.CotizacionRequestDTO;
import com.bca.conversion_exchange_rate.service.OrchestratorService;

@RestController
public class OrchestratorController implements ApiApi {

    private static final Logger logger = LoggerFactory.getLogger(OrchestratorController.class);
    private final OrchestratorService service;

    public OrchestratorController(OrchestratorService service) {
        this.service = service;
    }

    @Override
    public Mono<ResponseEntity<String>> simular(
            Mono<SimulacionRequestDTO> simulacionRequestDTO,
            final ServerWebExchange exchange) {
        return simulacionRequestDTO
                .flatMap(request -> {
                    logger.info("Solicitud de simulación recibida: {}", request);
                    return service.simulate(request)
                            .map(result -> ResponseEntity.ok(result))
                            .onErrorResume(error -> {
                                logger.error("Error en simulación: ", error);
                                return Mono.just(ResponseEntity.status(503)
                                        .body("Error en servicio externo: " + error.getMessage()));
                            });
                });
    }

    @Override
    public Mono<ResponseEntity<String>> cotizar(
            Mono<CotizacionRequestDTO> cotizacionRequestDTO,
            final ServerWebExchange exchange) {
        return cotizacionRequestDTO
                .flatMap(request -> {
                    logger.info("Solicitud de cotización recibida: {}", request);
                    return service.quote(request)
                            .map(result -> ResponseEntity.ok(result))
                            .onErrorResume(error -> {
                                logger.error("Error en cotización: ", error);
                                return Mono.just(ResponseEntity.status(503)
                                        .body("Error en servicio externo: " + error.getMessage()));
                            });
                });
    }

}