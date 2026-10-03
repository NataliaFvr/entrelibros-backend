package com.uade.entrelibros.backend.entity.dto;

import java.time.LocalDateTime;

import com.uade.entrelibros.backend.entity.Notificacion;
import lombok.Data;

@Data
public class NotificacionResponse {

    private Long id;
    private String tipo;
    private String mensaje;
    private Boolean leida;
    private LocalDateTime fecha;
    private Long idLibro;

    public static NotificacionResponse from(Notificacion notificacion) {
        NotificacionResponse r = new NotificacionResponse();
        r.id = notificacion.getId();
        r.tipo = notificacion.getTipo() != null ? notificacion.getTipo().name() : null;
        r.mensaje = notificacion.getMensaje();
        r.leida = notificacion.getLeida();
        r.fecha = notificacion.getFecha();
        if (notificacion.getLibro() != null) {
            r.idLibro = notificacion.getLibro().getId();
        }
        return r;
    }
}