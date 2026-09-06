package com.uade.entrelibros.backend.entity.dto;

import java.time.LocalDateTime;

import com.uade.entrelibros.backend.entity.Orden;
import lombok.Data;

@Data
public class OrdenResponse {

    private Long id;
    private LocalDateTime fecha;
    private String provinciaDestino;
    private Double subtotal;
    private Double total;
    private String estadoPago;
    private Long idComprador;
    private String nombreComprador;

    public static OrdenResponse from(Orden orden) {
        OrdenResponse r = new OrdenResponse();
        r.id = orden.getId();
        r.fecha = orden.getFecha();
        r.provinciaDestino = orden.getProvinciaDestino();
        r.subtotal = orden.getSubtotal();
        r.total = orden.getTotal();
        r.estadoPago = orden.getEstadoPago() != null ? orden.getEstadoPago().name() : null;
        if (orden.getComprador() != null) {
            r.idComprador = orden.getComprador().getId();
            r.nombreComprador = orden.getComprador().getNombre();
        }
        return r;
    }
}
