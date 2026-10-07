package com.uade.entrelibros.backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ZonaEnvio {
    MISMA_PROVINCIA,
    DISTINTA_PROVINCIA;

    @JsonCreator
    public static ZonaEnvio fromJson(String valor) {
        if (valor == null) {
            return null;
        }
        return switch (valor.trim().toLowerCase()) {
            case "misma", "misma_provincia" -> MISMA_PROVINCIA;
            case "distinta", "distinta_provincia" -> DISTINTA_PROVINCIA;
            default -> valueOf(valor.trim().toUpperCase());
        };
    }
}
