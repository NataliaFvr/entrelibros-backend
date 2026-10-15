package com.uade.entrelibros.backend.entity.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SolicitudVendedorRequest {

    @NotBlank(message = "Ingresá el nombre de tu tienda.")
    @Size(min = 2, max = 60, message = "Entre 2 y 60 caracteres.")
    private String nombreTienda;

    @NotBlank(message = "Ingresá tu teléfono.")
    @Pattern(regexp = "^(?=(?:\\D*\\d){6,15}\\D*$)[\\d\\s()+\\-]{6,20}$", message = "Ingresá un teléfono válido (solo números, espacios, + - o paréntesis; entre 6 y 15 dígitos).")
    private String telefono;

    // El maximo es 255 porque es el largo de la columna de la base
    @NotBlank(message = "Contanos qué libros vendés.")
    @Size(min = 10, max = 255, message = "Entre 10 y 255 caracteres.")
    private String descripcion;

    @NotBlank(message = "Elegí tu provincia.")
    @Pattern(regexp = "(?iu)^\\s*(?:|Buenos Aires|Ciudad Aut[oó]noma de Buenos Aires|CABA|Capital Federal|Catamarca|Chaco|Chubut|C[oó]rdoba|Corrientes|Entre R[ií]os|Formosa|Jujuy|La Pampa|La Rioja|Mendoza|Misiones|Neuqu[eé]n|R[ií]o Negro|Salta|San Juan|San Luis|Santa Cruz|Santa Fe|Santiago del Estero|Tierra del Fuego|Tucum[aá]n)\\s*$", message = "Elegí una provincia de la lista.")
    private String provincia;

    public void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda == null ? null : nombreTienda.strip();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? null : descripcion.strip();
    }
}