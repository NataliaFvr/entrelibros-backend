package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Notificacion {

    public Notificacion() {
    }

    public Notificacion(Usuario usuario, TipoNotificacion tipo, String mensaje, Libro libro) {
        this.usuario = usuario;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.libro = libro;
        this.leida = false;
        this.fecha = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    private Boolean leida;

    private LocalDateTime fecha;

    // Opcional: solo las notificaciones de moderacion de libro lo completan
    @ManyToOne
    @JoinColumn(name = "id_libro")
    private Libro libro;

    // Opcional: las notificaciones de compra y pago lo completan, para que el front pueda enlazar el pedido
    @ManyToOne
    @JoinColumn(name = "id_orden")
    private Orden orden;
}