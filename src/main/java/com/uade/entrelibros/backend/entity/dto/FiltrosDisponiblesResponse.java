package com.uade.entrelibros.backend.entity.dto;

import java.util.List;

import lombok.Data;

@Data
public class FiltrosDisponiblesResponse {
    private List<String> editoriales;
    private List<String> autores;
    private List<String> idiomas;
    private List<String> estadosLibro;
    private List<VendedorOptionResponse> vendedores;
}