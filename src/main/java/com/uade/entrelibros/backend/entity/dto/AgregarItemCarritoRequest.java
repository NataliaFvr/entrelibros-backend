package com.uade.entrelibros.backend.entity.dto;

import lombok.Data;

@Data
public class AgregarItemCarritoRequest {
    private Long idUsuario;
    private Long idLibro;
    private Integer cantidad;
}