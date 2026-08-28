package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class EnvioItem {

    public EnvioItem() {
    }

    public EnvioItem(OrdenVendedor ordenVendedor, Envio envio, Double costo) {
        this.ordenVendedor = ordenVendedor;
        this.envio = envio;
        this.costo = costo;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_orden_vendedor")
    private OrdenVendedor ordenVendedor;

    @ManyToOne
    @JoinColumn(name = "id_envio")
    private Envio envio;

    private Double costo;
}
