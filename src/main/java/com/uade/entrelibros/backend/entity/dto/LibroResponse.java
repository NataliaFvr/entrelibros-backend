package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.Libro;
import java.util.List;
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
    private List<String> categorias;
    private String nombreTienda;
    private String provinciaVendedor;
    private Integer vendidos;
    private String envio;

    public static LibroResponse from(Libro libro) {
        return from(libro, List.of(), null);
    }

    public static LibroResponse from(Libro libro, List<String> categorias) {
        return from(libro, categorias, null);
    }

    public static LibroResponse from(Libro libro, List<String> categorias, String provinciaComprador) {
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
            r.nombreTienda = libro.getVendedor().getNombreTienda() != null
                    && !libro.getVendedor().getNombreTienda().isBlank()
                    ? libro.getVendedor().getNombreTienda()
                    : libro.getVendedor().getNombre();
            r.provinciaVendedor = libro.getVendedor().getProvincia();
        }
        r.categorias = categorias != null ? categorias : List.of();
        r.vendidos = libro.getVendidos() != null ? libro.getVendidos() : 0;
        if (provinciaComprador != null && !provinciaComprador.isBlank() && libro.getVendedor() != null) {
            r.envio = com.uade.entrelibros.backend.service.EnvioPolicy
                    .determinarTipo(libro.getVendedor().getProvincia(), provinciaComprador)
                    == com.uade.entrelibros.backend.entity.ZonaEnvio.MISMA_PROVINCIA
                    ? "misma" : "distinta";
        }
        return r;
    }
}
