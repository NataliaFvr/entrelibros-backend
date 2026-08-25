package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Categoria {

    public Categoria() {
    }

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombre;
}
