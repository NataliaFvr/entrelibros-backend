package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
public class ResenaLibro {

    public ResenaLibro() {
    }

    public ResenaLibro(OrdenItem ordenItem, Integer calificacion, String comentario) {
        this.ordenItem = ordenItem;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fecha = LocalDate.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_orden_item")
    private OrdenItem ordenItem;

    private Integer calificacion;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    private LocalDate fecha;
}
