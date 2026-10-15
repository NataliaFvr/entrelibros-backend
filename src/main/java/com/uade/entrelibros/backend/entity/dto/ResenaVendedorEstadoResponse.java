package com.uade.entrelibros.backend.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResenaVendedorEstadoResponse {
    private boolean puedeResenar;
    private String motivo;
    private Long idPago;
    private ResenaVendedorResponse miResena;
}