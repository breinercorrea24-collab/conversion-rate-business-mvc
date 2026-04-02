package com.bca.conversion_business_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bca.conversion_business_service.api.CurrencyRateClient;
import com.bca.conversion_business_service.controller.mapper.Mapper;
import com.bca.conversion_business_service.dto.CotizacionDTO;
import com.bca.conversion_business_service.dto.SimulacionDTO;
import com.bca.conversion_business_service.dto.cotizacion.CotizacionRequestDTO;
import com.bca.conversion_business_service.dto.simulacion.SimulacionRequestDTO;
import com.bca.conversion_business_service.service.ClienteService;
import com.bca.conversion_business_service.service.CotizacionService;
import com.bca.conversion_business_service.service.SimulacionService;

import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Conversion API", description = "Endpoints para simulación y cotización de divisas")
public class ConversationRateController {
	private final CurrencyRateClient currencyRateClient;
	private final ClienteService clienteService;
	private final SimulacionService simulacionService;
	private final CotizacionService cotizacionService;

	public ConversationRateController(CurrencyRateClient currencyRateClient, ClienteService clienteService,
			SimulacionService simulacionService, CotizacionService cotizacionService) {
		this.currencyRateClient = currencyRateClient;
		this.clienteService = clienteService;
		this.simulacionService = simulacionService;
		this.cotizacionService = cotizacionService;
	}

	@Operation(summary = "Simular conversión", description = "Simula una conversión de divisas para un cliente y retorna la simulación guardada")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Simulación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),

			@ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json")),

			@ApiResponse(responseCode = "503", description = "Servicio externo no disponible", content = @Content(mediaType = "application/json"))
	})
	@PostMapping("/exchange-rate")
	public Mono<ResponseEntity<Map<String, Object>>> simular(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de simulación", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimulacionRequestDTO.class))) @RequestBody SimulacionRequestDTO request) {

		Predicate<SimulacionRequestDTO> validRequest = r -> r != null && r.getDatosCliente() != null
				&& r.getExchangeRate() != null
				&& r.getExchangeRate().getAmount() != null;

		if (!validRequest.test(request)) {
			return Mono.just(ResponseEntity.badRequest()
					.body(Map.of("error", "request.exchangeRate.amount is required")));
		}

		return clienteService.crearOActualizarCliente(request.getDatosCliente()).flatMap(clienteDTO -> {

			String to = request.getExchangeRate().getCurrency();
			String from = request.getExchangeRate().getTargetCurrency();
			Double amount = request.getExchangeRate().getAmount().doubleValue();

			return currencyRateClient.getRate(to, from, amount)

					.flatMap(exchangeRateResponseDTO -> {

						// 🔴 1. Validar si vino con error
						if (exchangeRateResponseDTO.getMessage() != null) {

							Map<String, Object> errorResponse = new HashMap<>();
							errorResponse.put("clienteDTO", clienteDTO);
							errorResponse.put("error", exchangeRateResponseDTO.getMessage());

							return Mono.just(ResponseEntity
									.status(HttpStatus.SERVICE_UNAVAILABLE)
									.body(errorResponse));
						}

						// 🟢 2. Si todo OK → procesar simulación
						SimulacionDTO simulacionDTO = Mapper.simulate(clienteDTO, exchangeRateResponseDTO);

						return simulacionService.saveSimulacion(simulacionDTO).map(savedSimulacion -> {
							Map<String, Object> response = new HashMap<>();
							response.put("clienteDTO", clienteDTO);
							response.put("simulacionDTO", savedSimulacion);
							return ResponseEntity.ok(response);
						});
					});
		});
	}

	@Operation(summary = "Crear cotización", description = "Guarda una cotización basada en la preferencia de tipo de cambio y operación")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Cotización creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),

			@ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json")),

			@ApiResponse(responseCode = "503", description = "Servicio externo no disponible", content = @Content(mediaType = "application/json"))
	})
	@PostMapping("/booking-rate")
	public Mono<ResponseEntity<Map<String, Object>>> cotizar(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para cotización", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CotizacionRequestDTO.class))) @RequestBody CotizacionRequestDTO request) {

		CotizacionDTO cotizacionDTO = Mapper.toCotizacionDTO(request);

		return cotizacionService.saveCotizacion(cotizacionDTO).map(savedCotizacion -> {
			Map<String, Object> response = new HashMap<>();
			response.put("status", savedCotizacion.getStatus());
			response.put("idCotizacion", savedCotizacion.getIdCotizacion());
			response.put("currency", savedCotizacion.getCurrency());
			response.put("targetCurrency", savedCotizacion.getTargetCurrency());
			return ResponseEntity.ok(response);
		});
	}
}
