package com.uade.entrelibros.backend.entity.dto;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Edicion parcial: un campo que no viene (null) no se toca. Si viene, tiene que cumplir su regla.
@Data
public class UsuarioUpdateRequest {

    @Size(min = 3, max = 20, message = "Entre 3 y 20 caracteres: letras, números, punto o guion bajo.")
    @Pattern(regexp = "^[A-Za-z0-9_.]+$", message = "Entre 3 y 20 caracteres: letras, números, punto o guion bajo.")
    private String nombreUsuario;

    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$", message = "Ingresá un e-mail válido (por ejemplo: nombre@dominio.com).")
    private String email;

    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$",
        message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un caracter especial"
    )
    private String contrasena;

    // Obligatoria cuando el propio usuario cambia su contrasena (el admin no la necesita)
    private String contrasenaActual;

    @Size(min = 2, max = 50, message = "Debe tener entre 2 y 50 caracteres.")
    @Pattern(regexp = "^[\\p{L}][\\p{L}\\s'\\u2019.-]*$", message = "Usá solo letras, espacios, apóstrofe o guion.")
    private String nombre;

    @Size(min = 2, max = 50, message = "Debe tener entre 2 y 50 caracteres.")
    @Pattern(regexp = "^[\\p{L}][\\p{L}\\s'\\u2019.-]*$", message = "Usá solo letras, espacios, apóstrofe o guion.")
    private String apellido;

    @Pattern(regexp = "(?iu)^\\s*(?:|Buenos Aires|Ciudad Aut[oó]noma de Buenos Aires|CABA|Capital Federal|Catamarca|Chaco|Chubut|C[oó]rdoba|Corrientes|Entre R[ií]os|Formosa|Jujuy|La Pampa|La Rioja|Mendoza|Misiones|Neuqu[eé]n|R[ií]o Negro|Salta|San Juan|San Luis|Santa Cruz|Santa Fe|Santiago del Estero|Tierra del Fuego|Tucum[aá]n)\\s*$", message = "Elegí una provincia de la lista.")
    private String provincia;

    @Pattern(regexp = "^(?=(?:\\D*\\d){6,15}\\D*$)[\\d\\s()+\\-]{6,20}$", message = "Ingresá un teléfono válido (solo números, espacios, + - o paréntesis; entre 6 y 15 dígitos).")
    private String telefono;

    // La columna es de 255 caracteres: pasarse es un error de validacion, no un error de base de datos
    @Size(max = 255, message = "Máximo 255 caracteres.")
    private String descripcion;

    // Clave del avatar elegido (los avatares por defecto estan como assets del front).
    // Mandar "" lo borra y el front vuelve a su avatar por defecto.
    @Size(max = 50, message = "La clave del avatar no puede superar los 50 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "La clave del avatar solo puede tener letras, números, guion y guion bajo")
    private String avatar;

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.strip();
    }

    public void setApellido(String apellido) {
        this.apellido = apellido == null ? null : apellido.strip();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? null : descripcion.strip();
    }
}