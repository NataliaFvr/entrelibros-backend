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
    private String tituloLibro;
    // Para que el front sepa cual es "mi" resena sin guardarlo en localStorage
    private Long idComprador;
    private String nombreComprador;
    private String apellidoComprador;

    public static ResenaLibroResponse from(ResenaLibro resena) {
        ResenaLibroResponse r = new ResenaLibroResponse();
        r.id = resena.getId();
        r.calificacion = resena.getCalificacion();
        r.comentario = resena.getComentario();
        r.fecha = resena.getFecha();
        r.idLibro = resena.getOrdenItem().getLibro().getId();
        r.tituloLibro = resena.getOrdenItem().getLibro().getTitulo();
        r.idComprador = resena.getOrdenItem().getOrden().getComprador().getId();
        r.nombreComprador = resena.getOrdenItem().getOrden().getComprador().getNombre();
        r.apellidoComprador = resena.getOrdenItem().getOrden().getComprador().getApellido();
        return r;
    }
}