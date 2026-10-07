package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.CarritoItem;
import com.uade.entrelibros.backend.entity.ImagenLibro;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Data;

@Data
public class CarritoItemResponse {

    private Long id;
    private Integer cantidad;
    private Long idLibro;
    private String tituloLibro;
    private Double precioUnitario;
    private Double subtotal;
    private String nombreTienda;
    private Integer stock;
    private Double descuentoPct;
    private String portada;

    public static CarritoItemResponse from(CarritoItem item) {
        return from(item, null);
    }

    public static CarritoItemResponse from(CarritoItem item, ImagenLibro portada) {
        CarritoItemResponse r = new CarritoItemResponse();
        r.id = item.getId();
        r.cantidad = item.getCantidad();
        if (item.getLibro() != null) {
            r.idLibro = item.getLibro().getId();
            r.tituloLibro = item.getLibro().getTitulo();
            Double precio = item.getLibro().getPrecio();
            Double descuento = item.getLibro().getDescuentoPct() != null
                    ? item.getLibro().getDescuentoPct() : 0.0;
            if (precio != null) {
                r.precioUnitario = BigDecimal.valueOf(precio)
                        .multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(descuento)
                                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)))
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
            }
            if (r.precioUnitario != null && item.getCantidad() != null) {
                r.subtotal = BigDecimal.valueOf(r.precioUnitario)
                        .multiply(BigDecimal.valueOf(item.getCantidad()))
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
            }
            r.descuentoPct = descuento;
            r.stock = item.getLibro().getStock();
            if (item.getLibro().getVendedor() != null) {
                String tienda = item.getLibro().getVendedor().getNombreTienda();
                r.nombreTienda = tienda != null && !tienda.isBlank()
                        ? tienda : item.getLibro().getVendedor().getNombre();
            }
        }
        if (portada != null) {
            r.portada = "/imagenes-libro/" + portada.getId() + "/contenido";
        }
        return r;
    }
}
