package com.uade.entrelibros.backend.service;

import java.util.List;
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
    public Usuario createUsuario(String nombreUsuario, String email, String contrasena, String nombre, String apellido);
    public Usuario createUsuario(String nombreUsuario, String email, String contrasena, String nombre,
            String apellido, Rol rol);
    public Usuario updateUsuario(Long usuarioId, UsuarioUpdateRequest request);
    public Usuario darDeBajaUsuario(Long usuarioId);
    public Usuario cambiarRol(Long usuarioId, Rol nuevoRol);
    public Usuario reactivarUsuario(Long usuarioId);
    public Usuario verificarEmail(String email, String codigo);
    public Usuario reenviarCodigoVerificacion(String email);
    public Usuario solicitarResetPassword(String email);
    public Usuario cambiarContrasenia(String email, String codigo, String nuevaContrasenia);

    public Usuario solicitarVendedor(Usuario usuario, String nombreTienda);
    public List<Usuario> getSolicitudesVendedorPendientes();
    public Usuario resolverSolicitudVendedor(Long usuarioId, boolean aprobar);
}