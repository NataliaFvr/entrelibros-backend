package com.uade.entrelibros.backend.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.UsuarioUpdateRequest;
import com.uade.entrelibros.backend.exceptions.UsuarioDuplicadoException;
import com.uade.entrelibros.backend.exceptions.UsuarioNoEncontradoException;

public interface UsuarioService {
    public Page<Usuario> getUsuarios(PageRequest pageRequest);

    public Optional<Usuario> getUsuarioById(Long usuarioId);

    public Usuario createUsuario(String nombreUsuario, String email, String contrasena, String nombre, String apellido)
            throws UsuarioDuplicadoException;

    public Usuario createUsuario(String nombreUsuario, String email, String contrasena, String nombre,
            String apellido, Rol rol) throws UsuarioDuplicadoException;

    public Usuario updateUsuario(Long usuarioId, UsuarioUpdateRequest request)
            throws UsuarioDuplicadoException, UsuarioNoEncontradoException;

    public void eliminarUsuario(Long usuarioId) throws UsuarioNoEncontradoException;
}