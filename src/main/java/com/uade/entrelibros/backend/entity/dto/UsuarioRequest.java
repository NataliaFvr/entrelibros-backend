package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.exceptions.CampoInvalidoException;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Lo usan el registro publico (POST /api/v1/auth/register) y el alta desde el panel admin (POST /usuarios).
// En el registro publico solo se leen los datos de cuenta: el rol siempre es COMPRADOR y los datos de
// vendedor se ignoran.
@Data
public class UsuarioRequest {

    @NotBlank(message = "Ingresá tu nombre de usuario.")
    @Size(min = 3, max = 20, message = "Entre 3 y 20 caracteres: letras, números, punto o guion bajo.")
    @Pattern(regexp = "^[A-Za-z0-9_.]+$", message = "Entre 3 y 20 caracteres: letras, números, punto o guion bajo.")
    private String nombreUsuario;

    @NotBlank(message = "Ingresá tu e-mail.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$", message = "Ingresá un e-mail válido (por ejemplo: nombre@dominio.com).")
    private String email;

    @NotBlank(message = "Ingresá tu contraseña.")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$",
        message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un caracter especial"
    )
    private String contrasena;

    // Segundo campo de "Repetir contraseña". Opcional: si el cliente lo manda, tiene que coincidir (ver validarRepeticion)
    private String repetirContrasena;

    @NotBlank(message = "Ingresá tu nombre.")
    @Size(min = 2, max = 50, message = "Debe tener entre 2 y 50 caracteres.")
    @Pattern(regexp = "^[\\p{L}][\\p{L}\\s'\\u2019.-]*$", message = "Usá solo letras, espacios, apóstrofe o guion.")
    private String nombre;

    @NotBlank(message = "Ingresá tu apellido.")
    @Size(min = 2, max = 50, message = "Debe tener entre 2 y 50 caracteres.")
    @Pattern(regexp = "^[\\p{L}][\\p{L}\\s'\\u2019.-]*$", message = "Usá solo letras, espacios, apóstrofe o guion.")
    private String apellido;

    // Solo para el alta desde el panel admin. Si no viene se crea un COMPRADOR.
    private Rol rol;

    // Datos de vendedor: solo se usan en el alta desde el panel admin con rol VENDEDOR (ahi son obligatorios)
    @Size(min = 2, max = 60, message = "Entre 2 y 60 caracteres.")
    private String nombreTienda;

    @Pattern(regexp = "(?iu)^\\s*(?:|Buenos Aires|Ciudad Aut[oó]noma de Buenos Aires|CABA|Capital Federal|Catamarca|Chaco|Chubut|C[oó]rdoba|Corrientes|Entre R[ií]os|Formosa|Jujuy|La Pampa|La Rioja|Mendoza|Misiones|Neuqu[eé]n|R[ií]o Negro|Salta|San Juan|San Luis|Santa Cruz|Santa Fe|Santiago del Estero|Tierra del Fuego|Tucum[aá]n)\\s*$", message = "Elegí una provincia de la lista.")
    private String provincia;

    @Pattern(regexp = "^(?:|(?=(?:\\D*\\d){6,15}\\D*$)[\\d\\s()+\\-]{6,20})$", message = "Ingresá un teléfono válido (solo números, espacios, + - o paréntesis; entre 6 y 15 dígitos).")
    private String telefono;

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.strip();
    }

    public void setApellido(String apellido) {
        this.apellido = apellido == null ? null : apellido.strip();
    }

    public void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda == null ? null : nombreTienda.strip();
    }

    // "Repetir contraseña" no se puede validar con anotaciones de un solo campo: lo llama el controller.
    // Error con el formato de siempre: { "repetirContrasena": "Las contraseñas no coinciden." }
    public void validarRepeticion() {
        if (repetirContrasena != null && !repetirContrasena.equals(contrasena)) {
            throw new CampoInvalidoException("repetirContrasena", "Las contraseñas no coinciden.");
        }
    }
}