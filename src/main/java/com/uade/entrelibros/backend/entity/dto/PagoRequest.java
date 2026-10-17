package com.uade.entrelibros.backend.entity.dto;

import lombok.Data;

@Data
public class PagoRequest {
    private Long idOrden;
    private String proveedor;

    // Solo cuando proveedor = "tarjeta". Se validan y se descartan: los datos de la tarjeta no se guardan.
    private String numeroTarjeta;
    private String titular;
    private String vencimiento; // MM/AA
    private String codigoSeguridad;
}
