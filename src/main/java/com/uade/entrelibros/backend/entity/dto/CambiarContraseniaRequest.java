package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.exceptions.CampoInvalidoException;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CambiarContraseniaRequest {

    @NotBlank(message = "Ingresá tu e-mail.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$", message = "Ingresá un e-mail válido (por ejemplo: nombre@dominio.com).")
    private String email;

    @NotBlank(message = "Ingresá los 6 dígitos del código.")
    @Pattern(regexp = "^\\d{6}$", message = "Ingresá los 6 dígitos del código.")
    private String codigo;

    @NotBlank(message = "Ingresá tu contraseña.")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$",
        message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un caracter especial"
    )
    private String nuevaContrasenia;

    // Segundo campo de "Repetir contraseña". Opcional: si el cliente lo manda, tiene que coincidir.
    private String repetirNuevaContrasenia;

    public void validarRepeticion() {
        if (repetirNuevaContrasenia != null && !repetirNuevaContrasenia.equals(nuevaContrasenia)) {
            throw new CampoInvalidoException("repetirNuevaContrasenia", "Las contraseñas no coinciden.");
        }
    }
}
