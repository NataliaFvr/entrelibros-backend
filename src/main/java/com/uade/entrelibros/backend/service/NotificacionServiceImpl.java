package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.EstadoUsuario;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Notificacion;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.TipoNotificacion;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.NotificacionInvalidaException;
import com.uade.entrelibros.backend.exceptions.NotificacionNoEncontradaException;

import com.uade.entrelibros.backend.repository.NotificacionRepository;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public Notificacion crear(Usuario destinatario, TipoNotificacion tipo, String mensaje, Libro libro) {
        if (destinatario == null || tipo == null || mensaje == null || mensaje.isBlank()) {
            throw new NotificacionInvalidaException();
        }
        return notificacionRepository.save(new Notificacion(destinatario, tipo, mensaje, libro));
    }

    @Override
    public Notificacion crearDeOrden(Usuario destinatario, TipoNotificacion tipo, String mensaje, Orden orden) {
        if (destinatario == null || tipo == null || mensaje == null || mensaje.isBlank() || orden == null) {
            throw new NotificacionInvalidaException();
        }
        Notificacion notificacion = new Notificacion(destinatario, tipo, mensaje, null);
        notificacion.setOrden(orden);
        return notificacionRepository.save(notificacion);
    }

    @Override
    public void notificarAdmins(TipoNotificacion tipo, String mensaje, Libro libro) {
        List<Usuario> admins = usuarioRepository.findByRolAndEstado(Rol.ADMIN, EstadoUsuario.ACTIVO);
        for (Usuario admin : admins) {
            crear(admin, tipo, mensaje, libro);
        }
    }

    @Override
    public Page<Notificacion> getNotificaciones(Long idUsuario, Pageable pageable) {
        Page<Notificacion> notificaciones = notificacionRepository
                .findByUsuarioIdOrderByFechaDesc(idUsuario, pageable);
        if (notificaciones.isEmpty()) {
            throw new ListaVaciaException("No tenes notificaciones");
        }
        return notificaciones;
    }

    @Override
    public Page<Notificacion> getNoLeidas(Long idUsuario, Pageable pageable) {
        Page<Notificacion> notificaciones = notificacionRepository
                .findByUsuarioIdAndLeidaFalseOrderByFechaDesc(idUsuario, pageable);
        if (notificaciones.isEmpty()) {
            throw new ListaVaciaException("No tenes notificaciones sin leer");
        }
        return notificaciones;
    }

    @Override
    @Transactional
    public int marcarLeidas(Long idUsuario) {
        int cantidad = notificacionRepository.marcarTodasComoLeidas(idUsuario);
        if (cantidad == 0) {
            throw new ListaVaciaException("No tenes notificaciones sin leer");
        }
        return cantidad;
    }

    @Override
    @Transactional
    public Notificacion marcarLeida(Long idUsuario, Long idNotificacion) {
        Notificacion notificacion = notificacionRepository.findByIdAndUsuarioId(idNotificacion, idUsuario)
                .orElseThrow(NotificacionNoEncontradaException::new);
        notificacion.setLeida(true);
        return notificacionRepository.save(notificacion);
    }

    @Override
    @Transactional
    public void eliminar(Long idUsuario, Long idNotificacion) {
        Notificacion notificacion = notificacionRepository.findByIdAndUsuarioId(idNotificacion, idUsuario)
                .orElseThrow(NotificacionNoEncontradaException::new);
        notificacionRepository.delete(notificacion);
    }

    @Override
    public long contarNoLeidas(Long idUsuario) {
        return notificacionRepository.countByUsuarioIdAndLeidaFalse(idUsuario);
    }
}