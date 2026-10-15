package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.EstadoModeracion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ModeracionRequest {

    @NotNull(message = "Elegí el estado de moderación.")
    private EstadoModeracion estadoModeracion;

    @Size(max = 500, message = "Máximo 500 caracteres.")
    private String comentario;

    public void setComentario(String comentario) {
        this.comentario = comentario == null ? null : comentario.strip();
    }
}