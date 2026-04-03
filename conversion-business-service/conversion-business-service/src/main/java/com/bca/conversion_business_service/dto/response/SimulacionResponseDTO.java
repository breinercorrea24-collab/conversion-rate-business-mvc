package com.bca.conversion_business_service.dto.response;

import com.bca.conversion_business_service.dto.ClienteDTO;
import com.bca.conversion_business_service.dto.SimulacionDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta exitosa de simulación de cambio")
public class SimulacionResponseDTO {

    @Schema(description = "Datos del cliente")
    private ClienteDTO clienteDTO;

    @Schema(description = "Datos de la simulación")
    private SimulacionDTO simulacionDTO;
}
