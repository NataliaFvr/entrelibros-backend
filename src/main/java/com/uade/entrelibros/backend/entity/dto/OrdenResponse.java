package com.uade.entrelibros.backend.entity.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.OrdenItem;
import lombok.Data;

@Data
public class OrdenResponse {

    private Long id;
    private LocalDateTime fecha;
    private String provinciaDestino;
    private Double subtotal;
    private Double costoEnvio;
    private Double total;
    private String estadoPago;
    private Long idComprador;
    private String nombreComprador;
    private List<OrdenItemResponse> items;

    public static OrdenResponse from(Orden orden, List<OrdenItem> ordenItems) {
        OrdenResponse r = from(orden);
        r.items = ordenItems.stream().map(OrdenItemResponse::from).toList();
        return r;
    }

    public static OrdenResponse from(Orden orden) {
        OrdenResponse r = new OrdenResponse();
        r.id = orden.getId();
        r.fecha = orden.getFecha();
        r.provinciaDestino = orden.getProvinciaDestino();
        r.subtotal = orden.getSubtotal();
        r.costoEnvio = orden.getCostoEnvio();
        r.total = orden.getTotal();
        r.estadoPago = orden.getEstadoPago() != null ? orden.getEstadoPago().name() : null;
        if (orden.getComprador() != null) {
            r.idComprador = orden.getComprador().getId();
            r.nombreComprador = orden.getComprador().getNombre();
        }
        return r;
    }
}