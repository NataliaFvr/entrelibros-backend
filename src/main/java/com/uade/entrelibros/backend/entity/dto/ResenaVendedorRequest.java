package com.uade.entrelibros.backend.entity.dto;

import lombok.Data;

@Data
public class ResenaVendedorRequest {
    private Long idEnvioItem;
    private Long idComprador;
    private Integer clasificacion;
    private String comentario;
}
