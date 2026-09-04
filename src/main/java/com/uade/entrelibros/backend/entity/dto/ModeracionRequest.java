package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.EstadoModeracion;
import lombok.Data;

@Data
public class ModeracionRequest {
    private EstadoModeracion estadoModeracion;
    private String comentario;
}