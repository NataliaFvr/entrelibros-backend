package com.uade.entrelibros.backend.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UsuarioUpdateRequest {

    private String nombreUsuario;

    @Email(message = "El email no tiene un formato válido")
    private String email;

    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$",
        message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un caracter especial"
    )
    private String contrasena;

    @Pattern(regexp = ".*\\S.*", message = "El nombre no puede estar vacío")
    private String nombre;

    @Pattern(regexp = ".*\\S.*", message = "El apellido no puede estar vacío")
    private String apellido;
}