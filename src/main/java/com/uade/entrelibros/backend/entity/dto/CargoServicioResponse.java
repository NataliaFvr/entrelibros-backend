package com.uade.entrelibros.backend.entity.dto;

import java.time.LocalDateTime;

import com.uade.entrelibros.backend.entity.ConfiguracionCargo;

import lombok.Data;

@Data
public class CargoServicioResponse {

    private Double porcentaje;
    
    private LocalDateTime fechaActualizacion;

    public static CargoServicioResponse from(ConfiguracionCargo config) {
        CargoServicioResponse r = new CargoServicioResponse();
        r.porcentaje = config.getPorcentaje();
        r.fechaActualizacion = config.getFechaActualizacion();
        return r;
    }
}