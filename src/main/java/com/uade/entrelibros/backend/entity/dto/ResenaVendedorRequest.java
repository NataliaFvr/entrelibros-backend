package com.uade.entrelibros.backend.entity.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Al crear, la calificacion y el comentario (minimo 3 caracteres) son obligatorios: los exige ResenaVendedorServiceImpl.
@Data
public class ResenaVendedorRequest {

    private Long idPago;

    private Long idVendedor;

    @Min(value = 1, message = "Elegí una calificación de 1 a 5 estrellas.")
    @Max(value = 5, message = "Elegí una calificación de 1 a 5 estrellas.")
    private Integer clasificacion;

    @Size(max = 300, message = "Máximo 300 caracteres.")
    private String comentario;

    public void setComentario(String comentario) {
        this.comentario = comentario == null ? null : comentario.strip();
    }
}