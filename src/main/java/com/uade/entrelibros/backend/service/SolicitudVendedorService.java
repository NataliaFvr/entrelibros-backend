package com.uade.entrelibros.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.entrelibros.backend.entity.EstadoSolicitud;
import com.uade.entrelibros.backend.entity.SolicitudVendedor;
import com.uade.entrelibros.backend.entity.Usuario;

public interface SolicitudVendedorService {

    SolicitudVendedor crear(Usuario usuario, String motivo);

    Page<SolicitudVendedor> getMisSolicitudes(Long idUsuario, Pageable pageable);

    Page<SolicitudVendedor> getSolicitudesPorEstado(EstadoSolicitud estado, Pageable pageable);

    SolicitudVendedor resolver(Long idSolicitud, EstadoSolicitud resolucion, String comentario, Usuario admin);
}