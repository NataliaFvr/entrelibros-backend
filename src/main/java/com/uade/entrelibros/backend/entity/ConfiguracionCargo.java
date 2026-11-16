package com.uade.entrelibros.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

//cargo servicio
@Data
@Entity
public class ConfiguracionCargo {

    public ConfiguracionCargo() {
    }

    public ConfiguracionCargo(Double porcentaje) {
        this.porcentaje = porcentaje;
        this.fechaActualizacion = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 5.0 = 5 %
    private Double porcentaje;

    private LocalDateTime fechaActualizacion;
}