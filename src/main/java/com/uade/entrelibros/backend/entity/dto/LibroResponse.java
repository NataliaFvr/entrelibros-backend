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
    private String snapshotTitulo;
    private String snapshotAutor;
    private String snapshotEditorial;
    private Integer snapshotAnio;
    private String snapshotIdioma;
    private String snapshotEstadoLibro;
    private Double snapshotPrecio;
    private Double snapshotDescuentoPct;
    private Integer snapshotStock;
    private String snapshotDescripcion;
    private java.time.LocalDateTime fechaSolicitudRevision;

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
            r.snapshotTitulo = libro.getSnapshotTitulo();
            r.snapshotAutor = libro.getSnapshotAutor();
            r.snapshotEditorial = libro.getSnapshotEditorial();
            r.snapshotAnio = libro.getSnapshotAnio();
            r.snapshotIdioma = libro.getSnapshotIdioma();
            r.snapshotEstadoLibro = libro.getSnapshotEstadoLibro() != null
                    ? libro.getSnapshotEstadoLibro().name() : null;
            r.snapshotPrecio = libro.getSnapshotPrecio();
            r.snapshotDescuentoPct = libro.getSnapshotDescuentoPct();
            r.snapshotStock = libro.getSnapshotStock();
            r.snapshotDescripcion = libro.getSnapshotDescripcion();
            r.fechaSolicitudRevision = libro.getFechaSolicitudRevision();
            return r;
    }
}
