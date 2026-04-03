package com.bca.conversion_exchange_rate.mapper;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public String map(String externalResponse) {
        // aquí transformarías JSON → DTO interno
        return externalResponse;
    }
}