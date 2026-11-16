package com.uade.entrelibros.backend.entity.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResolverSolicitudVendedorRequest {

    @NotNull(message = "Indicá si la solicitud se aprueba o se rechaza.")
    private Boolean aprobar;

    @Size(max = 500, message = "Máximo 500 caracteres.")
    private String comentario;

    public void setComentario(String comentario) {
        this.comentario = comentario == null ? null : comentario.strip();
    }
}
