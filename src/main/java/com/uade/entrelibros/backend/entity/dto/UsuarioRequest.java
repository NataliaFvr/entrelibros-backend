package com.uade.entrelibros.backend.entity.dto;


import com.uade.entrelibros.backend.entity.Rol;

import lombok.Data;

@Data
public class UsuarioRequest {
    private String nombreUsuario;
    private String email;
    private String contrasena;
    private String nombre;
    private String apellido;


    private Rol rol;


    }