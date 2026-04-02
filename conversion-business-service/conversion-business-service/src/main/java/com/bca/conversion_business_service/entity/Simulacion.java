package com.bca.conversion_business_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDate;

@Table("simulacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Simulacion {

    @Id
    private Long idSimulacion;

    private Long idCliente;  // ID del cliente para la relación

    private Double amount;
    private String currency;
    private String targetCurrency;

    private LocalDate date;
    private Double rate;
    private Double ratePreferente;
    private Double exchangeAmount;


    // Getters y setters
}