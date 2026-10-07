package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.Direccion;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.DireccionRequest;
import com.uade.entrelibros.backend.exceptions.DireccionNoEncontradaException;
import com.uade.entrelibros.backend.repository.DireccionRepository;

@Service
public class DireccionService {

    @Autowired
    private DireccionRepository direccionRepository;

    @Transactional
    public List<Direccion> getDirecciones(Long idUsuario) {
        List<Direccion> direcciones = direccionRepository.findByUsuarioId(idUsuario);
        if (!direcciones.isEmpty() && direcciones.stream().noneMatch(Direccion::isPrincipal)) {
            Direccion primera = direcciones.get(0);
            primera.setPrincipal(true);
            direccionRepository.save(primera);
        }
        return direcciones;
    }

    @Transactional
    public Direccion crear(Usuario usuario, DireccionRequest r) {
        Direccion direccion = new Direccion(usuario,
                r.getAlias().trim(), r.getCalle().trim(), r.getCiudad().trim(),
                r.getProvincia().trim(), r.getCp().trim());
        direccion.setPrincipal(direccionRepository.countByUsuarioId(usuario.getId()) == 0);
        return direccionRepository.save(direccion);
    }

    @Transactional
    public void eliminar(Long idUsuario, Long idDireccion) {
        Direccion d = direccionRepository.findByIdAndUsuarioId(idDireccion, idUsuario)
                .orElseThrow(DireccionNoEncontradaException::new);
        boolean eraPrincipal = d.isPrincipal();
        direccionRepository.delete(d);
        direccionRepository.flush();
        if (eraPrincipal) {
            List<Direccion> restantes = direccionRepository.findByUsuarioId(idUsuario);
            if (!restantes.isEmpty()) {
                direccionRepository.limpiarPrincipal(idUsuario);
                Direccion primera = restantes.get(0);
                primera.setPrincipal(true);
                direccionRepository.save(primera);
            }
        }
    }

    @Transactional
    public List<Direccion> marcarPrincipal(Long idUsuario, Long idDireccion) {
        Direccion elegida = direccionRepository.findByIdAndUsuarioId(idDireccion, idUsuario)
                .orElseThrow(DireccionNoEncontradaException::new);
        if (!elegida.isPrincipal()) {
            direccionRepository.limpiarPrincipal(idUsuario);
            elegida.setPrincipal(true);
            direccionRepository.save(elegida);
        }
        return direccionRepository.findByUsuarioId(idUsuario);
    }
}