package com.uade.entrelibros.backend.entity.dto;

import java.util.List;
import lombok.Data;

@Data
public class LibroFiltroRequest {
    private String texto;
    private List<Long> idCategorias;
    private Double precioMin;
    private Double precioMax;
    private List<String> editoriales;
    private List<String> autores;
    private List<String> idiomas;
    private List<Integer> anios;
    private Boolean soloConDescuento;
    private List<Long> idVendedores;
}