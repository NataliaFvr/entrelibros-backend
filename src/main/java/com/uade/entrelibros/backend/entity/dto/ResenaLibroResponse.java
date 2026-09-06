package com.uade.entrelibros.backend.entity.dto;

import java.time.LocalDate;

import com.uade.entrelibros.backend.entity.ResenaLibro;
import lombok.Data;

@Data
public class ResenaLibroResponse {

    private Long id;
    private Integer calificacion;
    private String comentario;
    private LocalDate fecha;
    private Long idLibro;
    private String nombreComprador;

    public static ResenaLibroResponse from(ResenaLibro resena) {
        ResenaLibroResponse r = new ResenaLibroResponse();
        r.id = resena.getId();
        r.calificacion = resena.getCalificacion();
        r.comentario = resena.getComentario();
        r.fecha = resena.getFecha();
        r.idLibro = resena.getOrdenItem().getLibro().getId();
        r.nombreComprador = resena.getOrdenItem().getOrden().getComprador().getNombre();
        return r;
    }
}
