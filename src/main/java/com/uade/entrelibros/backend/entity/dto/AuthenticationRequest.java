package com.uade.entrelibros.backend.entity.dto;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String email;
    private String contrasena;
} 