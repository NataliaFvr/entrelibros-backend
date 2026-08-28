package com.uade.entrelibros.backend.entity.dto;

import lombok.Data;

@Data
public class PagoRequest {
    private Long idOrden;
    private String proveedor;
}
