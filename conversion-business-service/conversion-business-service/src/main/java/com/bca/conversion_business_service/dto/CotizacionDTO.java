package com.bca.conversion_business_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionDTO {
    private Long idCotizacion;
    private Long idSimulacion; // ID del cliente asociado

    private BigDecimal amount;
    private String currency;
    private String targetCurrency;

    private LocalDate date;
    private String status;
    // Getters y setters

}