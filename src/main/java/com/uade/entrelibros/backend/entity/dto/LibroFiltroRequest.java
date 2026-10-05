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

    // NUEVO/USADO (valores del enum EstadoLibro, como String para no acoplar el DTO al enum)
    private List<String> estadosLibro;

    // bestsellers | nuevo | precioAsc | precioDesc | descuento
    private String sort;

    // Para el filtro de envío: se comparan contra la provincia cargada en el perfil del vendedor
    private String provinciaComprador;
    // true = solo vendedores de la misma provincia que provinciaComprador; false = solo de otras provincias
    private Boolean envioLocal;
}