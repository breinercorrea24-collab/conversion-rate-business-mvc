package com.bca.conversion_business_service.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDTO {

    private String from;
    private String to;
    private Double amount;
    private Double rate;
    private Double converted;
    private String message;

}
