package com.uade.entrelibros.backend.entity.dto;

import lombok.Data;

// Hoy no esta enlazada a ningun endpoint: la subida de imagenes es multipart
// (@RequestParam archivo/orden/idLibro en ImagenesLibroController).
@Data
public class ImagenLibroRequest {
    private Integer orden;
    private Long idLibro;
}