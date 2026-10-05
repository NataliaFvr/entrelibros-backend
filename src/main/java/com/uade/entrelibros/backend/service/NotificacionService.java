package com.uade.entrelibros.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Notificacion;
import com.uade.entrelibros.backend.entity.TipoNotificacion;
import com.uade.entrelibros.backend.entity.Usuario;

public interface NotificacionService {

    Notificacion crear(Usuario destinatario, TipoNotificacion tipo, String mensaje, Libro libro);

    void notificarAdmins(TipoNotificacion tipo, String mensaje, Libro libro);

    Page<Notificacion> getNotificaciones(Long idUsuario, Pageable pageable);

    Page<Notificacion> getNoLeidas(Long idUsuario, Pageable pageable);

    int marcarLeidas(Long idUsuario);
}