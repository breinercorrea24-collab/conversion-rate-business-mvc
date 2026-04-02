package com.bca.conversion_business_service.service;

import org.springframework.stereotype.Service;

import com.bca.conversion_business_service.dto.ClienteDTO;
import com.bca.conversion_business_service.entity.Cliente;
import com.bca.conversion_business_service.entity.Cotizacion;
import com.bca.conversion_business_service.entity.Simulacion;
import com.bca.conversion_business_service.entity.TipoCliente;
import com.bca.conversion_business_service.repository.ClienteRepository;
import com.bca.conversion_business_service.repository.CotizacionRepository;
import com.bca.conversion_business_service.repository.SimulacionRepository;
import com.bca.conversion_business_service.service.mapper.Mapper;
import com.bca.conversion_business_service.service.util.TasaPreferencial;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final CotizacionRepository cotizacionRepository;
    private final SimulacionRepository simulacionRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          CotizacionRepository cotizacionRepository,
                          SimulacionRepository simulacionRepository) {
        this.clienteRepository = clienteRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.simulacionRepository = simulacionRepository;
    }

    public Mono<ClienteDTO> obtenerClienteConDetalles(Long idCliente) {
        return clienteRepository.findById(idCliente)
                .flatMap(this::cargarCotizacionesYSimulaciones);
    }

    private Mono<ClienteDTO> cargarCotizacionesYSimulaciones(Cliente cliente) {
        Mono<List<Cotizacion>> cotizaciones = cotizacionRepository.findByIdCliente(cliente.getIdCliente()).collectList();
        Mono<List<Simulacion>> simulaciones = simulacionRepository.findByIdCliente(cliente.getIdCliente()).collectList();
        ClienteDTO clienteDTO = Mapper.convertirAClienteDTO(cliente);

        return Mono.zip(Mono.just(clienteDTO), cotizaciones, simulaciones)
                .map(tuple -> {
                    clienteDTO.setCotizaciones(Mapper.convertirACotizacionDTOList(tuple.getT2()));
                    clienteDTO.setSimulaciones(Mapper.convertirASimulacionDTOList(tuple.getT3()));
                    return clienteDTO;
                });
    }

    public Mono<ClienteDTO> crearOActualizarCliente(ClienteDTO clienteDTO) {
        
        // Supplier que crea un Cliente nuevo si no existe
        Supplier<Mono<Cliente>> createNewClienteSupplier = () -> {
            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setDni(clienteDTO.getDni());
            nuevoCliente.setNombre(clienteDTO.getNombre());
            nuevoCliente.setApellidos(clienteDTO.getApellidos());
            nuevoCliente.setTipoCliente(TipoCliente.fromString(clienteDTO.getTipoCliente()).name());
            nuevoCliente.setTasaPreferencial(TasaPreferencial.validateRate(clienteDTO));
            System.out.println("Creando nuevo cliente: " + nuevoCliente);
            return clienteRepository.save(nuevoCliente);
        };

        Consumer<Cliente> updateFromDto = existing -> {
            existing.setNombre(clienteDTO.getNombre());
            existing.setApellidos(clienteDTO.getApellidos());
            existing.setTipoCliente(clienteDTO.getTipoCliente());
            existing.setTasaPreferencial(TasaPreferencial.validateRate(clienteDTO));
        };

        return clienteRepository.findByDni(clienteDTO.getDni())
            .flatMap(existingCliente -> {
                updateFromDto.accept(existingCliente);
                return clienteRepository.save(existingCliente);
            })
            .switchIfEmpty(Mono.defer(createNewClienteSupplier))
            .map(Mapper::convertirAClienteDTO);
            
    }
    
}