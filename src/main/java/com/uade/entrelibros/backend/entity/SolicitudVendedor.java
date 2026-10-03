package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class SolicitudVendedor {

    public SolicitudVendedor() {
    }

    public SolicitudVendedor(Usuario usuario, String motivo) {
        this.usuario = usuario;
        this.motivo = motivo;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.fechaSolicitud = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    @Column(columnDefinition = "TEXT")
    private String comentarioAdmin;

    // Admin que resolvio la solicitud (null mientras esta pendiente)
    @ManyToOne
    @JoinColumn(name = "id_admin")
    private Usuario admin;

    private LocalDateTime fechaSolicitud;

    private LocalDateTime fechaResolucion;
}