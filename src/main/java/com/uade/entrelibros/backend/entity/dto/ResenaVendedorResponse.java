package com.uade.entrelibros.backend.entity.dto;

import java.time.LocalDate;

import com.uade.entrelibros.backend.entity.ResenaVendedor;
import lombok.Data;

@Data
public class ResenaVendedorResponse {

    private Long id;
    private Integer clasificacion;
    private String comentario;
    private LocalDate fecha;
    private Long idPago;
    private Long idVendedor;
    private String nombreComprador;

    public static ResenaVendedorResponse from(ResenaVendedor resena) {
        ResenaVendedorResponse r = new ResenaVendedorResponse();
        r.id = resena.getId();
        r.clasificacion = resena.getClasificacion();
        r.comentario = resena.getComentario();
        r.fecha = resena.getFecha();
        r.idPago = resena.getPago().getId();
        r.idVendedor = resena.getVendedor().getId();
        r.nombreComprador = resena.getComprador().getNombre();
        return r;
    }
}
