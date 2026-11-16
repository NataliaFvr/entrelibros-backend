package com.uade.entrelibros.backend.entity.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// PATCH /configuracion/cargo-servicio: { "porcentaje": 5 }
@Data
public class CargoServicioRequest {

    @NotNull(message = "Ingresá el porcentaje del cargo de servicio.")
    @DecimalMin(value = "0.0", message = "El porcentaje no puede ser negativo.")
    @DecimalMax(value = "30.0", message = "El porcentaje no puede superar el 30 %.")
    @Digits(integer = 2, fraction = 2, message = "El porcentaje admite hasta 2 decimales.")
    private Double porcentaje;
}