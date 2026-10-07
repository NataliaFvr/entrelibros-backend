package com.uade.entrelibros.backend.entity.dto;

import java.util.List;

import com.uade.entrelibros.backend.entity.OrdenItem;
import lombok.Data;

@Data
public class OrdenItemResponse {

    private Long idOrdenItem;
    private Long idLibro;
    private String tituloLibro;
    private String estadoLibro;
    private List<String> categorias;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    private Long idVendedor;
    private String nombreVendedor;

    public static OrdenItemResponse from(OrdenItem item) {
        return from(item, List.of());
    }

    public static OrdenItemResponse from(OrdenItem item, List<String> categorias) {
        OrdenItemResponse r = new OrdenItemResponse();
        r.idOrdenItem = item.getId();
        if (item.getLibro() != null) {
            r.idLibro = item.getLibro().getId();
            r.tituloLibro = item.getLibro().getTitulo();
            r.estadoLibro = item.getLibro().getEstadoLibro() != null
                    ? item.getLibro().getEstadoLibro().name() : null;
        }
        r.categorias = categorias != null ? categorias : List.of();
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
