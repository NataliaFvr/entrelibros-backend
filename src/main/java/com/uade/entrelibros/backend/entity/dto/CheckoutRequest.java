package com.uade.entrelibros.backend.entity.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Long idUsuario;
    private String provinciaDestino;
}