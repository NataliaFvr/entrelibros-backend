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
    private Double precioFinal;
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
    private String snapshotTitulo;
    private String snapshotAutor;
    private String snapshotEditorial;
    private Integer snapshotAnio;
    private String snapshotIdioma;
    private String snapshotEstadoLibro;
    private Double snapshotPrecio;
    private Double snapshotPrecioFinal;
    private Double snapshotDescuentoPct;
    private Integer snapshotStock;
    private String snapshotDescripcion;
    private java.time.LocalDateTime fechaSolicitudRevision;
    // URL relativa de la imagen de portada (la primera por orden), con la misma forma que CarritoItemResponse.portada.
    // null si el libro no tiene imagenes.
    private String portada;

    public LibroResponse conPortada(Long idImagen) {
        if (idImagen != null) {
            this.portada = "/imagenes-libro/" + idImagen + "/contenido";
        }
        return this;
    }

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
        double descuento = libro.getDescuentoPct() != null ? libro.getDescuentoPct() : 0.0;
        r.precioFinal = libro.getPrecio() == null ? null
                : java.math.BigDecimal.valueOf(libro.getPrecio())
                        .multiply(java.math.BigDecimal.ONE.subtract(
                                java.math.BigDecimal.valueOf(descuento)
                                        .divide(java.math.BigDecimal.valueOf(100), 10,
                                                java.math.RoundingMode.HALF_UP)))
                        .setScale(2, java.math.RoundingMode.HALF_UP)
                        .doubleValue();
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
        r.snapshotTitulo = libro.getSnapshotTitulo();
        r.snapshotAutor = libro.getSnapshotAutor();
        r.snapshotEditorial = libro.getSnapshotEditorial();
        r.snapshotAnio = libro.getSnapshotAnio();
        r.snapshotIdioma = libro.getSnapshotIdioma();
        r.snapshotEstadoLibro = libro.getSnapshotEstadoLibro() != null
                ? libro.getSnapshotEstadoLibro().name() : null;
        r.snapshotPrecio = libro.getSnapshotPrecio();
        r.snapshotPrecioFinal = libro.getSnapshotPrecio() == null ? null
                : java.math.BigDecimal.valueOf(libro.getSnapshotPrecio())
                        .multiply(java.math.BigDecimal.ONE.subtract(
                                java.math.BigDecimal.valueOf(
                                        libro.getSnapshotDescuentoPct() != null ? libro.getSnapshotDescuentoPct() : 0.0)
                                        .divide(java.math.BigDecimal.valueOf(100), 10,
                                                java.math.RoundingMode.HALF_UP)))
                        .setScale(2, java.math.RoundingMode.HALF_UP)
                        .doubleValue();
        r.snapshotDescuentoPct = libro.getSnapshotDescuentoPct();
        r.snapshotStock = libro.getSnapshotStock();
        r.snapshotDescripcion = libro.getSnapshotDescripcion();
        r.fechaSolicitudRevision = libro.getFechaSolicitudRevision();
        return r;
    }
}