package com.bca.currency_rate_service.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.bca.currency_rate_service.api.dto.CurrencyRateResponseDTO;
import com.bca.currency_rate_service.service.RateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@Component
@Tag(name = "Rates", description = "Operaciones con tasas")
public class RateHandler {

    private final RateService rateService;

    public RateHandler(RateService rateService) {
        this.rateService = rateService;
    }

    @Operation(
        summary = "Obtener tasa de cambio",
        parameters = {
            @Parameter(name = "from", in = ParameterIn.QUERY, description = "Moneda origen", required = true, schema = @Schema(type = "string")),
            @Parameter(name = "to", in = ParameterIn.QUERY, description = "Moneda destino", required = true, schema = @Schema(type = "string")),
            @Parameter(name = "amount", in = ParameterIn.QUERY, description = "Monto a convertir", required = true, schema = @Schema(type = "number", format = "double"))
        }
    )
    @ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Tasa de cambio obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyRateResponseDTO.class))),

			@ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalErrorResponse.class))),

			@ApiResponse(responseCode = "503", description = "Servicio externo no disponible", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalErrorResponse.class)))
	})
    public Mono<ServerResponse> getRate(ServerRequest request) {
        String from = request.queryParam("from").orElse("USD");
        String to   = request.queryParam("to").orElse("EUR");
        Double amount   = Double.valueOf(request.queryParam("amount").orElse("1"));

        return rateService.getRate(from, to, amount)
            .flatMap(dto -> ServerResponse.ok().bodyValue(dto));
    }
}