package com.bca.conversion_business_service.entity;

public enum TipoCliente {
    NATURAL("Natural"),
    JURIDICA("Juridica");

    private final String tipoCliente;

    TipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public static TipoCliente fromString(String tipoCliente) {
        for (TipoCliente tipo : TipoCliente.values()) {
            if (tipo.getTipoCliente().equalsIgnoreCase(tipoCliente)) {
                return tipo;
            }
        }
        return NATURAL; // Default value
    }
}