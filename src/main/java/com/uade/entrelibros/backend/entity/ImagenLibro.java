package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ImagenLibro {

    public ImagenLibro() {
    }

    public ImagenLibro(String url, Integer orden, Libro libro) {
        this.url = url;
        this.orden = orden;
        this.libro = libro;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private Integer orden;

    @ManyToOne
    @JoinColumn(name = "id_libro")
    private Libro libro;
}