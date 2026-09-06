package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.CarritoItem;
import lombok.Data;

@Data
public class CarritoItemResponse {

    private Long id;
    private Integer cantidad;
    private Long idLibro;
    private String tituloLibro;
    private Double precioUnitario;
    private Double subtotal;

    public static CarritoItemResponse from(CarritoItem item) {
        CarritoItemResponse r = new CarritoItemResponse();
        r.id = item.getId();
        r.cantidad = item.getCantidad();
        if (item.getLibro() != null) {
            r.idLibro = item.getLibro().getId();
            r.tituloLibro = item.getLibro().getTitulo();
            r.precioUnitario = item.getLibro().getPrecio();
            if (item.getLibro().getPrecio() != null && item.getCantidad() != null) {
                r.subtotal = item.getLibro().getPrecio() * item.getCantidad();
            }
        }
        return r;
    }
}
