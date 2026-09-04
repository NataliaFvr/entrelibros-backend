package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.EstadoModeracion;
import com.uade.entrelibros.backend.entity.HistorialModeracion;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistorialModeracionResponse {

    private Long id;
    private Long idLibro;
    private String tituloLibro;
    private Long idModerador;
    private String nombreModerador;
    private EstadoModeracion estadoAnterior;
    private EstadoModeracion estadoNuevo;
    private String comentario;
    private LocalDateTime fecha;

    public HistorialModeracionResponse(HistorialModeracion historial) {
        this.id = historial.getId();
        this.idLibro = historial.getLibro().getId();
        this.tituloLibro = historial.getLibro().getTitulo();
        this.idModerador = historial.getModerador().getId();
        this.nombreModerador = historial.getModerador().getNombre() + " " + historial.getModerador().getApellido();
        this.estadoAnterior = historial.getEstadoAnterior();
        this.estadoNuevo = historial.getEstadoNuevo();
        this.comentario = historial.getComentario();
        this.fecha = historial.getFecha();
    }
}