package com.bca.conversion_business_service.service.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.bca.conversion_business_service.dto.ClienteDTO;
import com.bca.conversion_business_service.dto.CotizacionDTO;
import com.bca.conversion_business_service.dto.SimulacionDTO;
import com.bca.conversion_business_service.entity.Cliente;
import com.bca.conversion_business_service.entity.Cotizacion;
import com.bca.conversion_business_service.entity.Simulacion;

public class Mapper {

    public static Cliente convertirACliente(ClienteDTO clienteDTO) {
    	
        Cliente cliente = new Cliente();
        cliente.setIdCliente(clienteDTO.getIdCliente());
        cliente.setTipoCliente(clienteDTO.getTipoCliente());
        cliente.setDni(clienteDTO.getDni());
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setApellidos(clienteDTO.getApellidos());
        cliente.setTasaPreferencial(clienteDTO.getTasaPreferencial());
        
        return cliente;
    }

    public static ClienteDTO convertirAClienteDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setIdCliente(cliente.getIdCliente());
        dto.setTipoCliente(cliente.getTipoCliente()); 
        dto.setDni(cliente.getDni());
        dto.setNombre(cliente.getNombre());
        dto.setApellidos(cliente.getApellidos());
        dto.setTasaPreferencial(cliente.getTasaPreferencial());
        return dto;
    }
    
    public static SimulacionDTO convertirASimulacionDTO(Simulacion simulacion) {
        SimulacionDTO dto = new SimulacionDTO();
        dto.setIdSimulacion(simulacion.getIdSimulacion());
        dto.setIdCliente(simulacion.getIdCliente());
        
        dto.setAmount(simulacion.getAmount());
        dto.setCurrency(simulacion.getCurrency());
        dto.setTargetCurrency(simulacion.getTargetCurrency());
        dto.setDate(simulacion.getDate());
        dto.setRate(simulacion.getRate());
        dto.setRatePreferente(simulacion.getRatePreferente());
        dto.setExchangeAmount(simulacion.getExchangeAmount());
        
        return dto;
    }

    public static Simulacion convertirASimulacion(SimulacionDTO simulacionDTO) {
        Simulacion simulacion = new Simulacion();
        simulacion.setIdSimulacion(simulacionDTO.getIdSimulacion());
        simulacion.setIdCliente(simulacionDTO.getIdCliente());
        
        simulacion.setAmount(simulacionDTO.getAmount());
        simulacion.setCurrency(simulacionDTO.getCurrency());
        simulacion.setTargetCurrency(simulacionDTO.getTargetCurrency());
        
        simulacion.setDate(simulacionDTO.getDate());
        simulacion.setRate(simulacionDTO.getRate());
        simulacion.setRatePreferente(simulacionDTO.getRatePreferente());
        simulacion.setExchangeAmount(simulacionDTO.getExchangeAmount());
        
        return simulacion;
    }

    public static CotizacionDTO convertirACotizacionDTO(Cotizacion cotizacion) {
        CotizacionDTO dto = new CotizacionDTO();
        dto.setIdCotizacion(cotizacion.getIdCotizacion());
        dto.setIdSimulacion(cotizacion.getIdSimulacion());
        
        dto.setAmount(cotizacion.getAmount());
        dto.setCurrency(cotizacion.getCurrency());
        dto.setTargetCurrency(cotizacion.getTargetCurrency());
        
        dto.setDate(cotizacion.getDate());
        dto.setStatus(cotizacion.getStatus());
        return dto;
    }

    public static Cotizacion convertirACotizacion(CotizacionDTO cotizacionDTO) {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setIdCotizacion(cotizacionDTO.getIdCotizacion());
        cotizacion.setIdSimulacion(cotizacionDTO.getIdSimulacion());
        
        cotizacion.setAmount(cotizacionDTO.getAmount());
        cotizacion.setCurrency(cotizacionDTO.getCurrency());
        cotizacion.setTargetCurrency(cotizacionDTO.getTargetCurrency());

        cotizacion.setDate(cotizacionDTO.getDate());
        cotizacion.setStatus(cotizacionDTO.getStatus());
        return cotizacion;
    }

    public static List<CotizacionDTO> convertirACotizacionDTOList(List<Cotizacion> cotizaciones) {
        return cotizaciones.stream()
                .map(Mapper::convertirACotizacionDTO)
                .collect(Collectors.toList());
    }

    public static List<SimulacionDTO> convertirASimulacionDTOList(List<Simulacion> simulaciones) {
        return simulaciones.stream()
                .map(Mapper::convertirASimulacionDTO)
                .collect(Collectors.toList());
    }
}