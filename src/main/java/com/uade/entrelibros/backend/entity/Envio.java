package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Envio {

    public Envio() {
    }

    public Envio(ZonaEnvio zona, Double costoFijo) {
        this.zona = zona;
        this.costoFijo = costoFijo;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ZonaEnvio zona;

    private Double costoFijo;
}
