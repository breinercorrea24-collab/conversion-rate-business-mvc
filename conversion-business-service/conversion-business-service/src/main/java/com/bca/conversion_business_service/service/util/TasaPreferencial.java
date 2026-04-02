package com.bca.conversion_business_service.service.util;

import java.util.function.Predicate;

import com.bca.conversion_business_service.dto.ClienteDTO;
import com.bca.conversion_business_service.entity.TipoCliente;

public class TasaPreferencial {

    private TasaPreferencial() { }
    
    public static Double validateRate(ClienteDTO clienteDTO) {

        Predicate<String> isValidDni = dni -> dni != null && dni.length() >= 8;

        if (!isValidDni.test(clienteDTO.getDni())) {
            throw new IllegalArgumentException("El DNI debe tener al menos 8 caracteres.");
        }

        int lastDigit = Character.getNumericValue(clienteDTO.getDni().charAt(7));

        Double baseRate = clienteDTO.getTipoCliente().equals(TipoCliente.JURIDICA.getTipoCliente())
                ? 0.010
                : 0.020;

        Double additionalRate = lastDigit * 0.01;

        return baseRate + additionalRate;
    }
}
