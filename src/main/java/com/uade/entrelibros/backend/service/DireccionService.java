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

    public List<Direccion> getDirecciones(Long idUsuario) {
        return direccionRepository.findByUsuarioId(idUsuario);
    }

    public Direccion crear(Usuario usuario, DireccionRequest r) {
        return direccionRepository.save(new Direccion(usuario,
                r.getAlias().trim(), r.getCalle().trim(), r.getCiudad().trim(),
                r.getProvincia().trim(), r.getCp().trim()));
    }

    @Transactional
    public void eliminar(Long idUsuario, Long idDireccion) {
        Direccion d = direccionRepository.findByIdAndUsuarioId(idDireccion, idUsuario)
                .orElseThrow(DireccionNoEncontradaException::new);
        direccionRepository.delete(d);
    }
}