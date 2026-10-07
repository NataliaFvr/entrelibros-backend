package com.uade.entrelibros.backend.entity.dto;
 
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
 
@Data
public class SolicitudVendedorRequest {
 
    @NotBlank(message = "El nombre de la tienda es obligatorio")
    private String nombreTienda;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "La provincia es obligatoria")
    private String provincia;
}