package com.uade.entrelibros.backend.entity.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SolicitudVendedorRequest {

    // Opcional: por que quiere ser vendedor
    @Size(max = 500, message = "El motivo no puede superar los 500 caracteres")
    private String motivo;
}