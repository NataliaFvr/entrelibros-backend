package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class HistorialModeracion {

    public HistorialModeracion() {
    }

    public HistorialModeracion(Libro libro, Usuario moderador, EstadoModeracion estadoAnterior,
            EstadoModeracion estadoNuevo, String comentario) {
        this.libro = libro;
        this.moderador = moderador;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.comentario = comentario;
        this.fecha = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_libro")
    private Libro libro;

    @ManyToOne
    @JoinColumn(name = "id_moderador")
    private Usuario moderador;

    @Enumerated(EnumType.STRING)
    private EstadoModeracion estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoModeracion estadoNuevo;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    private LocalDateTime fecha;
}