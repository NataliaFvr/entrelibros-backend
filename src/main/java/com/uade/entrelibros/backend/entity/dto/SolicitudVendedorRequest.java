package com.uade.entrelibros.backend.entity.dto;
 
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
 
@Data
public class SolicitudVendedorRequest {
 
    @NotBlank(message = "El nombre de la tienda es obligatorio")
    private String nombreTienda;
}