package com.uade.entrelibros.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.UsuarioDuplicadoException;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Page<Usuario> getUsuarios(PageRequest pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Optional<Usuario> getUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId);
    }

    public Usuario createUsuario(String nombreUsuario, String email, String contrasena, String nombre, String apellido)
            throws UsuarioDuplicadoException {
        List<Usuario> existentes = usuarioRepository.findByEmailOrNombreUsuario(email, nombreUsuario);
        if (existentes.isEmpty())
            return usuarioRepository.save(new Usuario(nombreUsuario, email, contrasena, nombre, apellido));
        throw new UsuarioDuplicadoException();
    }
}