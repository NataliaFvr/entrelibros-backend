package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
public class ResenaVendedor {

    public ResenaVendedor() {
    }

    public ResenaVendedor(EnvioItem envioItem, Usuario comprador, Integer clasificacion, String comentario) {
        this.envioItem = envioItem;
        this.comprador = comprador;
        this.clasificacion = clasificacion;
        this.comentario = comentario;
        this.fecha = LocalDate.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_envio_item")
    private EnvioItem envioItem;

    @ManyToOne
    @JoinColumn(name = "id_comprador")
    private Usuario comprador;

    private Integer clasificacion;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    private LocalDate fecha;
}
