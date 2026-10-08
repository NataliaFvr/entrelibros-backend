package com.uade.entrelibros.backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ZonaEnvio {
    MISMA_PROVINCIA,
    DISTINTA_PROVINCIA;

    // Acepta "misma" / "distinta" (lo que usa el front) o el nombre del enum, sin distinguir mayusculas.
    // Un valor desconocido devuelve null y el servicio responde ZonaEnvioInvalidaException ({"error": ...})
    // en lugar de un 400 generico de Jackson.
    @JsonCreator
    public static ZonaEnvio fromJson(String valor) {
        if (valor == null) {
            return null;
        }
        return switch (valor.trim().toLowerCase()) {
            case "misma", "misma_provincia" -> MISMA_PROVINCIA;
            case "distinta", "distinta_provincia" -> DISTINTA_PROVINCIA;
            default -> null;
        };
    }

    // "misma" | "distinta": la clave que usa el front
    public String getClave() {
        return this == MISMA_PROVINCIA ? "misma" : "distinta";
    }
}