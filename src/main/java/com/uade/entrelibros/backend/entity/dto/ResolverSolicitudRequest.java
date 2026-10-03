package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResolverSolicitudRequest {

    @NotNull(message = "La resolucion es obligatoria (APROBADA o RECHAZADA)")
    private EstadoSolicitud estado;

    @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
    private String comentario;
}