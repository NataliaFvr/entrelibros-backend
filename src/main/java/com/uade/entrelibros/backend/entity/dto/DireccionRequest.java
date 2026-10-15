package com.uade.entrelibros.backend.entity.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DireccionRequest {

    @NotBlank(message = "Ponele un nombre (Casa, Trabajo…).")
    @Size(min = 2, max = 30, message = "Entre 2 y 30 caracteres.")
    private String alias;

    @NotBlank(message = "Ingresá la calle y el número.")
    @Size(min = 4, max = 100, message = "Entre 4 y 100 caracteres.")
    @Pattern(regexp = "(?i).*(\\d|\\bs/?n\\b).*", message = "Agregá el número de la calle (o \"S/N\").")
    private String calle;

    @NotBlank(message = "Ingresá la ciudad o localidad.")
    @Size(min = 2, max = 60, message = "Entre 2 y 60 caracteres.")
    private String ciudad;

    @NotBlank(message = "Elegí la provincia.")
    @Pattern(regexp = "(?iu)^\\s*(?:|Buenos Aires|Ciudad Aut[oó]noma de Buenos Aires|CABA|Capital Federal|Catamarca|Chaco|Chubut|C[oó]rdoba|Corrientes|Entre R[ií]os|Formosa|Jujuy|La Pampa|La Rioja|Mendoza|Misiones|Neuqu[eé]n|R[ií]o Negro|Salta|San Juan|San Luis|Santa Cruz|Santa Fe|Santiago del Estero|Tierra del Fuego|Tucum[aá]n)\\s*$", message = "Elegí una provincia de la lista.")
    private String provincia;

    @NotBlank(message = "Ingresá el código postal.")
    @Pattern(regexp = "^([A-Za-z]\\d{4}[A-Za-z]{3}|\\d{4})$", message = "Ingresá 4 dígitos (ej.: 1425) o el formato C1425ABC.")
    private String cp;

    public void setAlias(String alias) {
        this.alias = alias == null ? null : alias.strip();
    }

    public void setCalle(String calle) {
        this.calle = calle == null ? null : calle.strip();
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad == null ? null : ciudad.strip();
    }

    public void setCp(String cp) {
        this.cp = cp == null ? null : cp.strip();
    }
}