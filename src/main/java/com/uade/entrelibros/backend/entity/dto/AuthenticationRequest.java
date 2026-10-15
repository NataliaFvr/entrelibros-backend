package com.uade.entrelibros.backend.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotBlank(message = "Ingresá tu e-mail.")
    private String email;

    @NotBlank(message = "Ingresá tu contraseña.")
    private String contrasena;
}