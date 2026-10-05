package com.uade.entrelibros.backend.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerificarEmailRequest {

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "El código es obligatorio")
    private String codigo;
}