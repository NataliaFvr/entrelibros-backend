package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
public class Carrito {

    public Carrito() {
    }

    public Carrito(Usuario usuario) {
        this.usuario = usuario;
        this.fechaCreacion = LocalDate.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private LocalDate fechaCreacion;
}