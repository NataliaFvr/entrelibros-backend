package com.uade.entrelibros.backend.entity.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

// GET /carrito/envio: lo que costaria el carrito con esa direccion, antes de confirmar la compra
@Data
@AllArgsConstructor
public class CotizacionEnvioResponse {
    private Double subtotal;
    private Double costoEnvio;
    private Double total;
    // Un envio por vendedor: cuantos envios se cobran
    private Integer cantidadEnvios;
    // El detalle de cada envio, para mostrar en el resumen de que vendedor sale y cuanto cuesta
    private List<EnvioVendedor> envios;

    // zona: "misma" o "distinta" provincia que la direccion elegida
    public record EnvioVendedor(Long idVendedor, String vendedor, String zona, Double costo) {
    }
}
