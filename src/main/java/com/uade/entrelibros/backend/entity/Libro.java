package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Libro {

    public Libro() {
    }

    public Libro(String titulo, String autor, String editorial, Integer anio, String idioma,
            EstadoLibro estadoLibro, Double precio, Double descuentoPct, Integer stock,
            String descripcion, Usuario vendedor) {
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anio = anio;
        this.idioma = idioma;
        this.estadoLibro = estadoLibro;
        this.precio = precio;
        this.descuentoPct = descuentoPct;
        this.stock = stock;
        this.descripcion = descripcion;
        this.vendedor = vendedor;
        this.estadoPublicacion = EstadoPublicacion.ACTIVA;
        this.estadoModeracion = EstadoModeracion.EN_REVISION;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String autor;
    private String editorial;
    private Integer anio;
    private String idioma;

    @Enumerated(EnumType.STRING)
    private EstadoLibro estadoLibro;

    private Double precio;
    private Double descuentoPct;
    private Integer stock;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_vendedor")
    private Usuario vendedor;

    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estadoPublicacion;

    @Enumerated(EnumType.STRING)
    private EstadoModeracion estadoModeracion;
}