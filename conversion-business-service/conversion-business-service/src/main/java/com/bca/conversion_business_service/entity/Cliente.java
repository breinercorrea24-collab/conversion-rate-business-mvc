package com.bca.conversion_business_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("cliente")  // Anotación de R2DBC para la tabla
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    private Long idCliente;

    private String tipoCliente;
    private String dni;
    private String nombre;
    private String apellidos;

    private Double tasaPreferencial;

    // No definimos directamente las relaciones, ya que R2DBC no las soporta.
    // Puedes gestionarlas manualmente en el servicio.

    // Getters y setters
}