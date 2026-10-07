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
    private String provincia;
    private String nombreTienda;
    private String telefono;
    private String descripcion;
    private String estadoSolicitudVendedor;
    private String avatar;
    private boolean tieneFoto;

    public static UsuarioResponse from(Usuario usuario) {
        UsuarioResponse r = new UsuarioResponse();
        r.id = usuario.getId();
        r.nombreUsuario = usuario.getNombreUsuario();
        r.nombre = usuario.getNombre();
        r.apellido = usuario.getApellido();
        r.email = usuario.getEmail();
        r.rol = usuario.getRol().name();
        r.estado = usuario.getEstado().name();
        r.provincia = usuario.getProvincia();
        r.nombreTienda = usuario.getNombreTienda();
        r.telefono = usuario.getTelefono();
        r.descripcion = usuario.getDescripcion();
        r.estadoSolicitudVendedor = usuario.getEstadoSolicitudVendedor() != null
                ? usuario.getEstadoSolicitudVendedor().name() : null;
        r.avatar = usuario.getAvatar();
        r.tieneFoto = Boolean.TRUE.equals(usuario.getTieneFoto());
        return r;
    }
}