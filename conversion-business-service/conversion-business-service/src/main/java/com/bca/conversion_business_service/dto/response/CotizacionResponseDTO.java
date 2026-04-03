package com.bca.conversion_business_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta exitosa de cotización")
public class CotizacionResponseDTO {

    @Schema(description = "Estado de la cotización")
    private String status;

    @Schema(description = "ID de la cotización")
    private Long idCotizacion;

    @Schema(description = "Moneda origen")
    private String currency;

    @Schema(description = "Moneda destino")
    private String targetCurrency;
}
