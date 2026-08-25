package com.uade.entrelibros.backend.entity.dto;

import java.util.List;

import lombok.Data;

@Data
public class LibroRequest {
    private String titulo;
    private String autor;
    private String editorial;
    private Integer anio;
    private String idioma;
    private String estadoLibro;
    private Double precio;
    private Double descuentoPct;
    private Integer stock;
    private String descripcion;
    private Long idVendedor;
    private List<Long> idCategorias;
}