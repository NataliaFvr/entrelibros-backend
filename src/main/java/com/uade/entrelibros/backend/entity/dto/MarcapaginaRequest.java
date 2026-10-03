package com.uade.entrelibros.backend.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MarcapaginaRequest {

    @NotNull(message = "El id del libro es obligatorio")
    private Long idLibro;
}