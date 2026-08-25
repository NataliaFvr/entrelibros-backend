package com.uade.entrelibros.backend.entity.dto;

import lombok.Data;

@Data
public class ImagenLibroRequest {
    private String url;
    private Integer orden;
    private Long idLibro;
}