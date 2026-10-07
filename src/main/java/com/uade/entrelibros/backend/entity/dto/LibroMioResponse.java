package com.uade.entrelibros.backend.entity.dto;

import java.util.List;

import com.uade.entrelibros.backend.entity.Libro;

import lombok.Data;

@Data
public class LibroMioResponse extends LibroResponse {

    private String motivoRechazo;

    public static LibroMioResponse from(Libro libro, List<String> categorias, String motivoRechazo) {
        LibroResponse base = LibroResponse.from(libro, categorias);
        LibroMioResponse response = new LibroMioResponse();
        response.setId(base.getId());
        response.setTitulo(base.getTitulo());
        response.setAutor(base.getAutor());
        response.setEditorial(base.getEditorial());
        response.setAnio(base.getAnio());
        response.setIdioma(base.getIdioma());
        response.setEstadoLibro(base.getEstadoLibro());
        response.setPrecio(base.getPrecio());
        response.setDescuentoPct(base.getDescuentoPct());
        response.setStock(base.getStock());
        response.setDescripcion(base.getDescripcion());
        response.setEstadoPublicacion(base.getEstadoPublicacion());
        response.setEstadoModeracion(base.getEstadoModeracion());
        response.setIdVendedor(base.getIdVendedor());
        response.setNombreVendedor(base.getNombreVendedor());
        response.setCategorias(base.getCategorias());
        response.setNombreTienda(base.getNombreTienda());
        response.setProvinciaVendedor(base.getProvinciaVendedor());
        response.setVendidos(base.getVendidos());
        response.setMotivoRechazo(motivoRechazo);
        return response;
    }
}
