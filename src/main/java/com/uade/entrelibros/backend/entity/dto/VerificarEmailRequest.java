package com.uade.entrelibros.backend.entity.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerificarEmailRequest {

    @NotBlank(message = "Ingresá tu e-mail.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$", message = "Ingresá un e-mail válido (por ejemplo: nombre@dominio.com).")
    private String email;

    @NotBlank(message = "Ingresá los 6 dígitos del código.")
    @Pattern(regexp = "^\\d{6}$", message = "Ingresá los 6 dígitos del código.")
    private String codigo;
}
