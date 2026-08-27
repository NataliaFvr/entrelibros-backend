package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class OrdenVendedor {

    public OrdenVendedor() {
    }

    public OrdenVendedor(Orden orden, Usuario vendedor) {
        this.orden = orden;
        this.vendedor = vendedor;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_orden")
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "id_vendedor")
    private Usuario vendedor;
}