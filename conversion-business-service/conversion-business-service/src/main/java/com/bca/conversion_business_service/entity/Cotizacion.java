package com.bca.conversion_business_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Table("cotizacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cotizacion {

    @Id
    private Long idCotizacion;
    private Long idSimulacion;
    private Long idCliente;

    private BigDecimal amount;
    private String currency;
    private String targetCurrency;

    private LocalDate date;
    
    private String status;
    // Getters y setters

}