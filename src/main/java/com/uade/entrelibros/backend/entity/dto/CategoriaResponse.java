package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.Categoria;
import lombok.Data;

@Data
public class CategoriaResponse {

    private Long id;
    private String nombre;
    private boolean activa;
    // true si la categoria tiene una imagen activa: el front pide GET /categorias/{id}/imagen,
    // si es false dibuja el circulo de color por defecto
    private boolean tieneImagen;

    public static CategoriaResponse from(Categoria categoria, boolean tieneImagen) {
        CategoriaResponse r = new CategoriaResponse();
        r.id = categoria.getId();
        r.nombre = categoria.getNombre();
        r.activa = categoria.isActiva();
        r.tieneImagen = tieneImagen;
        return r;
    }
}
