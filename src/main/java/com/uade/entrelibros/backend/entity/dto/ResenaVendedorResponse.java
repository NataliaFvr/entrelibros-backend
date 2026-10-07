package com.uade.entrelibros.backend.entity.dto;

import java.time.LocalDate;

import com.uade.entrelibros.backend.entity.ResenaVendedor;
import com.uade.entrelibros.backend.entity.OrdenItem;
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
    private String apellidoComprador;
    private Long idLibro;
    private String libro;

    public static ResenaVendedorResponse from(ResenaVendedor resena) {
        return from(resena, null);
    }

    public static ResenaVendedorResponse from(ResenaVendedor resena, OrdenItem item) {
        ResenaVendedorResponse r = new ResenaVendedorResponse();
        r.id = resena.getId();
        r.clasificacion = resena.getClasificacion();
        r.comentario = resena.getComentario();
        r.fecha = resena.getFecha();
        r.idPago = resena.getPago().getId();
        r.idVendedor = resena.getVendedor().getId();
        r.nombreComprador = resena.getComprador().getNombre();
        r.apellidoComprador = resena.getComprador().getApellido();
        if (item != null && item.getLibro() != null) {
            r.idLibro = item.getLibro().getId();
            r.libro = item.getLibro().getTitulo();
        }
        return r;
    }
}
