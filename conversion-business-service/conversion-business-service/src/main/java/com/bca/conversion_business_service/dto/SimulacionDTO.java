package com.bca.conversion_business_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacionDTO {
    private Long idSimulacion;
    private Long idCliente; // ID del cliente asociado

    private Double amount;
    private String currency;
    private String targetCurrency;

    private LocalDate date;
    private Double rate;
    private Double ratePreferente;
    private Double exchangeAmount;
}