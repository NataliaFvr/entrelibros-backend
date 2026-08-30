package com.uade.entrelibros.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.UsuarioDuplicadoException;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<Usuario> getUsuarios(PageRequest pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Optional<Usuario> getUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId);
    }

    public Usuario createUsuario(String nombreUsuario, String email, String contrasena, String nombre,
            String apellido) throws UsuarioDuplicadoException {
        return createUsuario(nombreUsuario, email, contrasena, nombre, apellido, Rol.COMPRADOR);
    }

    public Usuario createUsuario(String nombreUsuario, String email, String contrasena, String nombre,
            String apellido, Rol rol) throws UsuarioDuplicadoException {
        List<Usuario> existentes = usuarioRepository.findByEmailOrNombreUsuario(email, nombreUsuario);
        if (!existentes.isEmpty())
            throw new UsuarioDuplicadoException();

        String contrasenaHasheada = passwordEncoder.encode(contrasena);
        return usuarioRepository.save(
                new Usuario(nombreUsuario, email, contrasenaHasheada, nombre, apellido, rol));
    }
}