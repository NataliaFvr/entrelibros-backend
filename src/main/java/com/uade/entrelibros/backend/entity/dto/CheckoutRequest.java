package com.uade.entrelibros.backend.entity.dto;

import lombok.Data;

// El usuario NO viaja en el body: se toma del token (@AuthenticationPrincipal en CarritoController)
@Data
public class CheckoutRequest {
    private String provinciaDestino;
    private Long idDireccion;
}