package com.bca.conversion_business_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long idCliente;
    private String tipoCliente;
    private String dni;
    private String nombre;
    private String apellidos;
    private Double tasaPreferencial;
    private List<CotizacionDTO> cotizaciones;
    private List<SimulacionDTO> simulaciones;

    // Getters y setters
}