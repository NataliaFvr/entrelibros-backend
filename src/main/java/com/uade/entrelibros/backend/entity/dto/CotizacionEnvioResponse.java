package com.uade.entrelibros.backend.entity.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

// total = subtotal (libros) + costoEnvio + cargoServicio
@Data
@AllArgsConstructor
public class CotizacionEnvioResponse {
    private Double subtotal;
    private Double costoEnvio;
    // Cargo de servicio de la plataforma: porcentajeCargo % sobre el subtotal de los libros (sin envio)
    private Double cargoServicio;
    private Double porcentajeCargo;
    private Double total;
    private Integer cantidadEnvios;
    private List<EnvioVendedor> envios;

    // zona: "misma" o "distinta" provincia que la direccion elegida
    public record EnvioVendedor(Long idVendedor, String vendedor, String zona, Double costo) {
    }
}