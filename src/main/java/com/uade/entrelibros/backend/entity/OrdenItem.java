package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class OrdenItem {

    public OrdenItem() {
    }

    public OrdenItem(Orden orden, Libro libro, Usuario vendedor, Integer cantidad, Double precioUnitario) {
        this.orden = orden;
        this.libro = libro;
        this.vendedor = vendedor;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_orden")
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "id_libro")
    private Libro libro;

    @ManyToOne
    @JoinColumn(name = "id_vendedor")
    private Usuario vendedor;

    private Integer cantidad;
    private Double precioUnitario;
}