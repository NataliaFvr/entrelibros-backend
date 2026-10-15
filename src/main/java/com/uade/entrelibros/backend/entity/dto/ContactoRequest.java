package com.uade.entrelibros.backend.entity.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactoRequest {

    @NotBlank(message = "Ingresá tu nombre.")
    @Size(min = 2, max = 50, message = "Debe tener entre 2 y 50 caracteres.")
    @Pattern(regexp = "^[\\p{L}][\\p{L}\\s'\\u2019.-]*$", message = "Usá solo letras, espacios, apóstrofe o guion.")
    private String nombre;

    @NotBlank(message = "Ingresá tu apellido.")
    @Size(min = 2, max = 50, message = "Debe tener entre 2 y 50 caracteres.")
    @Pattern(regexp = "^[\\p{L}][\\p{L}\\s'\\u2019.-]*$", message = "Usá solo letras, espacios, apóstrofe o guion.")
    private String apellido;

    @NotBlank(message = "Ingresá un e-mail válido.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$", message = "Ingresá un e-mail válido (por ejemplo: nombre@dominio.com).")
    private String email;

    @Pattern(regexp = "^(?:|\\+?\\d{1,5})$", message = "Ej.: +54 o 11 (solo números).")
    private String area;

    @Pattern(regexp = "^(?:|(?=(?:\\D*\\d){6,15}\\D*$)[\\d\\s()+\\-]{6,20})$", message = "Ingresá un teléfono válido (solo números, espacios, + - o paréntesis; entre 6 y 15 dígitos).")
    private String telefono;

    @Pattern(regexp = "^(Compras|Ventas|Envíos|Mi cuenta|Otro)$", message = "Elegí un departamento de la lista.")
    private String departamento;

    @NotBlank(message = "Escribí tu mensaje.")
    @Size.List({
        @Size(min = 10, message = "Contanos un poco más (mínimo 10 caracteres)."),
        @Size(max = 1000, message = "Máximo 1000 caracteres.")
    })
    private String mensaje;

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.strip();
    }

    public void setApellido(String apellido) {
        this.apellido = apellido == null ? null : apellido.strip();
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje == null ? null : mensaje.strip();
    }
}