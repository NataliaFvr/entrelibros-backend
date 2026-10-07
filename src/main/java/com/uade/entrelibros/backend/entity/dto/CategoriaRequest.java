package com.uade.entrelibros.backend.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaRequest {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombre;
}
