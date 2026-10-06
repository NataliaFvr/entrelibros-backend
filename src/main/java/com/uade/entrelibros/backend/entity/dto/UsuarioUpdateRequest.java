package com.uade.entrelibros.backend.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    private String provincia;

    // Clave del avatar elegido (los avatares por defecto estan como assets del front).
    // Mandar "" lo borra y el front vuelve a su avatar por defecto.
    @Size(max = 50, message = "La clave del avatar no puede superar los 50 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "La clave del avatar solo puede tener letras, números, guion y guion bajo")
    private String avatar;
}