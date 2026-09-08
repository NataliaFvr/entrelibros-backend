package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.OrdenItem;
import lombok.Data;

@Data
public class OrdenItemResponse {

    private Long idOrdenItem;
    private Long idLibro;
    private String tituloLibro;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    private Long idVendedor;
    private String nombreVendedor;

    public static OrdenItemResponse from(OrdenItem item) {
        OrdenItemResponse r = new OrdenItemResponse();
        r.idOrdenItem = item.getId();
        if (item.getLibro() != null) {
            r.idLibro = item.getLibro().getId();
            r.tituloLibro = item.getLibro().getTitulo();
        }
        r.cantidad = item.getCantidad();
        r.precioUnitario = item.getPrecioUnitario();
        if (item.getCantidad() != null && item.getPrecioUnitario() != null) {
            r.subtotal = item.getPrecioUnitario() * item.getCantidad();
        }
        if (item.getVendedor() != null) {
            r.idVendedor = item.getVendedor().getId();
            r.nombreVendedor = item.getVendedor().getNombre();
        }
        return r;
    }
}
