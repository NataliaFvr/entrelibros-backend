package com.uade.entrelibros.backend.entity.dto;

import lombok.Data;

@Data
public class ResenaVendedorRequest {
    private Long idPago;
    private Integer clasificacion;
    private String comentario;
}
