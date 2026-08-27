package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CarritoItem {

    public CarritoItem() {
    }

    public CarritoItem(Carrito carrito, Libro libro, Integer cantidad) {
        this.carrito = carrito;
        this.libro = libro;
        this.cantidad = cantidad;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_carrito")
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "id_libro")
    private Libro libro;

    private Integer cantidad;
}