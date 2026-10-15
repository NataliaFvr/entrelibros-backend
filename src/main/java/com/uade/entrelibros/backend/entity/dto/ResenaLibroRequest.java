package com.uade.entrelibros.backend.entity.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResenaLibroRequest {

    private Long idOrdenItem;

    @Min(value = 1, message = "Elegí una puntuación de 1 a 5.")
    @Max(value = 5, message = "Elegí una puntuación de 1 a 5.")
    private Integer calificacion;

    @Size(max = 500, message = "Máximo 500 caracteres.")
    private String comentario;

    public void setComentario(String comentario) {
        this.comentario = comentario == null ? null : comentario.strip();
    }
}