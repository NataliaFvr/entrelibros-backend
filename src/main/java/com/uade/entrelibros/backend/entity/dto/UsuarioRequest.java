package com.uade.entrelibros.backend.entity.dto;

import lombok.Data;

@Data
public class UsuarioRequest {
    private String nombreUsuario;
    private String email;
    private String contrasena;
    private String nombre;
    private String apellido;
}
