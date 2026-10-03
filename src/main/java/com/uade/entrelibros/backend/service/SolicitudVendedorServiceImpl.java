package com.uade.entrelibros.backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.EstadoSolicitud;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.SolicitudVendedor;
import com.uade.entrelibros.backend.entity.TipoNotificacion;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.ResolucionInvalidaException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.exceptions.SolicitudNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.SolicitudPendienteExistenteException;
import com.uade.entrelibros.backend.exceptions.SolicitudYaResueltaException;
import com.uade.entrelibros.backend.exceptions.YaEsVendedorException;
import com.uade.entrelibros.backend.repository.SolicitudVendedorRepository;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

@Service
public class SolicitudVendedorServiceImpl implements SolicitudVendedorService {

    @Autowired
    private SolicitudVendedorRepository solicitudVendedorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacionService notificacionService;

    @Override
    @Transactional
    public SolicitudVendedor crear(Usuario usuario, String motivo) {
        if (usuario.getRol() == Rol.VENDEDOR) {
            throw new YaEsVendedorException();
        }

        if (usuario.getRol() != Rol.COMPRADOR) {
            throw new RolInvalidoException();
        }
        if (solicitudVendedorRepository.existsByUsuarioIdAndEstado(usuario.getId(), EstadoSolicitud.PENDIENTE)) {
            throw new SolicitudPendienteExistenteException();
        }

        String motivoLimpio = motivo != null && !motivo.isBlank() ? motivo.trim() : null;
        SolicitudVendedor solicitud = solicitudVendedorRepository
                .save(new SolicitudVendedor(usuario, motivoLimpio));

        notificacionService.notificarAdmins(TipoNotificacion.SOLICITUD_VENDEDOR_PENDIENTE,
                usuario.getNombre() + " " + usuario.getApellido() + " solicito ser vendedor", null);

        return solicitud;
    }

    @Override
    public Page<SolicitudVendedor> getMisSolicitudes(Long idUsuario, Pageable pageable) {
        Page<SolicitudVendedor> solicitudes = solicitudVendedorRepository
                .findByUsuarioIdOrderByFechaSolicitudDesc(idUsuario, pageable);
        if (solicitudes.isEmpty()) {
            throw new ListaVaciaException("No tenes solicitudes para ser vendedor");
        }
        return solicitudes;
    }

    @Override
    public Page<SolicitudVendedor> getSolicitudesPorEstado(EstadoSolicitud estado, Pageable pageable) {
        Page<SolicitudVendedor> solicitudes = solicitudVendedorRepository
                .findByEstadoOrderByFechaSolicitudAsc(estado, pageable);
        if (solicitudes.isEmpty()) {
            throw new ListaVaciaException("No hay solicitudes de vendedor en ese estado");
        }
        return solicitudes;
    }


    @Override
    @Transactional
    public SolicitudVendedor resolver(Long idSolicitud, EstadoSolicitud resolucion, String comentario,
            Usuario admin) {
        if (resolucion == null || resolucion == EstadoSolicitud.PENDIENTE) {
            throw new ResolucionInvalidaException();
        }

        SolicitudVendedor solicitud = solicitudVendedorRepository.findByIdConCandado(idSolicitud)
                .orElseThrow(SolicitudNoEncontradaException::new);

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new SolicitudYaResueltaException();
        }

        String comentarioLimpio = comentario != null && !comentario.isBlank() ? comentario.trim() : null;

        solicitud.setEstado(resolucion);
        solicitud.setComentarioAdmin(comentarioLimpio);
        solicitud.setAdmin(admin);
        solicitud.setFechaResolucion(LocalDateTime.now());

        Usuario solicitante = solicitud.getUsuario();

        if (resolucion == EstadoSolicitud.APROBADA) {
            solicitante.setRol(Rol.VENDEDOR);
            usuarioRepository.save(solicitante);
            notificacionService.crear(solicitante, TipoNotificacion.VENDEDOR_APROBADO,
                    "Tu solicitud fue aprobada. Ya sos vendedor y podes publicar libros", null);
        } else {
            String motivo = comentarioLimpio != null ? " Motivo: " + comentarioLimpio : "";
            notificacionService.crear(solicitante, TipoNotificacion.VENDEDOR_RECHAZADO,
                    "Tu solicitud para ser vendedor fue rechazada." + motivo, null);
        }

        return solicitudVendedorRepository.save(solicitud);
    }
}
