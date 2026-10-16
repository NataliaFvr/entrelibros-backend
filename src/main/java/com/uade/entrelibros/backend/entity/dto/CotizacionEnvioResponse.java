package com.uade.entrelibros.backend.entity.dto;

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
}
