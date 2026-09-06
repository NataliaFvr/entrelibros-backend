package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.Usuario;
import lombok.Data;

@Data
public class UsuarioResponse {

    private Long id;
    private String nombreUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private String estado;

    public static UsuarioResponse from(Usuario usuario) {
        UsuarioResponse r = new UsuarioResponse();
        r.id = usuario.getId();
        r.nombreUsuario = usuario.getNombreUsuario();
        r.nombre = usuario.getNombre();
        r.apellido = usuario.getApellido();
        r.email = usuario.getEmail();
        r.rol = usuario.getRol().name();
        r.estado = usuario.getEstado().name();
        return r;
    }
}
