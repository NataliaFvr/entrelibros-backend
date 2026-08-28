package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Pago {

    public Pago() {
    }

    public Pago(Orden orden, String proveedor) {
        this.orden = orden;
        this.proveedor = proveedor;
        this.resultado = EstadoPago.SIMULADO_APROBADO;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String proveedor;

    @Enumerated(EnumType.STRING)
    private EstadoPago resultado;

    @ManyToOne
    @JoinColumn(name = "id_orden")
    private Orden orden;
}
