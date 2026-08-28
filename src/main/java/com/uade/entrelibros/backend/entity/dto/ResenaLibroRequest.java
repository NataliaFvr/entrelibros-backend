package com.uade.entrelibros.backend.entity.dto;

import lombok.Data;

@Data
public class ResenaLibroRequest {
    private Long idOrdenItem;
    private Integer calificacion;
    private String comentario;
}
