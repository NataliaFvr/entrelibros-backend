package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ImagenLibro {

    public ImagenLibro() {
    }

    public ImagenLibro(byte[] imagen, String tipoContenido, Integer orden, Libro libro) {
        this.imagen = imagen;
        this.tipoContenido = tipoContenido;
        this.orden = orden;
        this.libro = libro;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imagen;

    private String tipoContenido; // "image/jpeg" o "image/png"

    private Integer orden;

    @ManyToOne
    @JoinColumn(name = "id_libro")
    private Libro libro;
}