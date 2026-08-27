package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Orden {

    public Orden() {
    }

    public Orden(Usuario comprador, String provinciaDestino, Double subtotal, Double total) {
        this.comprador = comprador;
        this.fecha = LocalDateTime.now();
        this.provinciaDestino = provinciaDestino;
        this.subtotal = subtotal;
        this.total = total;
        this.estadoPago = EstadoPago.SIMULADO_APROBADO;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_comprador")
    private Usuario comprador;

    private LocalDateTime fecha;
    private String provinciaDestino;
    private Double subtotal;
    private Double total;

    @Enumerated(EnumType.STRING)
    private EstadoPago estadoPago;
}