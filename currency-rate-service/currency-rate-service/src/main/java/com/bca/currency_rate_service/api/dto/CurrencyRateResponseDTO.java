package com.bca.currency_rate_service.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyRateResponseDTO {
     private String from;        // Moneda origen
    private String to;           // Moneda destino
    private double amount;       // Monto original que llega del request
    private double rate;         // Tipo de cambio aplicado
    private double converted;    // Monto resultante de la conversión
}
