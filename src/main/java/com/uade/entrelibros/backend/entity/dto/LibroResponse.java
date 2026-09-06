package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.Libro;
import lombok.Data;

@Data
public class LibroResponse {

    private Long id;
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
    private String estadoPublicacion;
    private String estadoModeracion;
    private Long idVendedor;
    private String nombreVendedor;

    public static LibroResponse from(Libro libro) {
        LibroResponse r = new LibroResponse();
        r.id = libro.getId();
        r.titulo = libro.getTitulo();
        r.autor = libro.getAutor();
        r.editorial = libro.getEditorial();
        r.anio = libro.getAnio();
        r.idioma = libro.getIdioma();
        r.estadoLibro = libro.getEstadoLibro() != null ? libro.getEstadoLibro().name() : null;
        r.precio = libro.getPrecio();
        r.descuentoPct = libro.getDescuentoPct();
        r.stock = libro.getStock();
        r.descripcion = libro.getDescripcion();
        r.estadoPublicacion = libro.getEstadoPublicacion() != null ? libro.getEstadoPublicacion().name() : null;
        r.estadoModeracion = libro.getEstadoModeracion() != null ? libro.getEstadoModeracion().name() : null;
        if (libro.getVendedor() != null) {
            r.idVendedor = libro.getVendedor().getId();
            r.nombreVendedor = libro.getVendedor().getNombre();
        }
        return r;
    }
}
