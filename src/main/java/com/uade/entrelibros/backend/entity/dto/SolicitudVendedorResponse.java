package com.uade.entrelibros.backend.entity.dto;

import java.time.LocalDateTime;

import com.uade.entrelibros.backend.entity.SolicitudVendedor;
import lombok.Data;

@Data
public class SolicitudVendedorResponse {

    private Long id;
    private Long idUsuario;
    private String nombreUsuario;
    private String email;
    private String motivo;
    private String estado;
    private String comentarioAdmin;
    private Long idAdmin;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaResolucion;

    public static SolicitudVendedorResponse from(SolicitudVendedor solicitud) {
        SolicitudVendedorResponse r = new SolicitudVendedorResponse();
        r.id = solicitud.getId();
        if (solicitud.getUsuario() != null) {
            r.idUsuario = solicitud.getUsuario().getId();
            r.nombreUsuario = solicitud.getUsuario().getNombre() + " " + solicitud.getUsuario().getApellido();
            r.email = solicitud.getUsuario().getEmail();
        }
        r.motivo = solicitud.getMotivo();
        r.estado = solicitud.getEstado() != null ? solicitud.getEstado().name() : null;
        r.comentarioAdmin = solicitud.getComentarioAdmin();
        if (solicitud.getAdmin() != null) {
            r.idAdmin = solicitud.getAdmin().getId();
        }
        r.fechaSolicitud = solicitud.getFechaSolicitud();
        r.fechaResolucion = solicitud.getFechaResolucion();
        return r;
    }
}