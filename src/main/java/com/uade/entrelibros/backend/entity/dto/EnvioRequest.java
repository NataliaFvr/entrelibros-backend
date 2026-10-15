package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.ZonaEnvio;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnvioRequest {

    private ZonaEnvio zona;

    @NotNull(message = "Ingresá el costo del envío.")
    @DecimalMin(value = "0.0", inclusive = false, message = "El costo debe ser mayor a 0.")
    @DecimalMax(value = "1000000", message = "El costo no puede superar $1.000.000.")
    private Double costoFijo;
}