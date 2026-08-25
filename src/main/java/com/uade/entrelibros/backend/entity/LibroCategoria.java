package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class LibroCategoria {

    public LibroCategoria() {
    }

    public LibroCategoria(Libro libro, Categoria categoria) {
        this.libro = libro;
        this.categoria = categoria;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_libro")
    private Libro libro;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
}