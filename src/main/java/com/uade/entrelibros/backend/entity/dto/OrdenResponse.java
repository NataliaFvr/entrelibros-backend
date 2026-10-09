package com.uade.entrelibros.backend.entity.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    private String apellidoComprador;
    private String proveedor;
    private List<OrdenItemResponse> items;
    private LocalDateTime venceEn;
    private String calleDestino;
    private String ciudadDestino;
    private String cpDestino;

    public static OrdenResponse from(Orden orden, List<OrdenItem> ordenItems) {
        return from(orden, ordenItems, null);
    }

    public static OrdenResponse from(Orden orden, List<OrdenItem> ordenItems, String proveedor) {
        return from(orden, ordenItems, proveedor, Map.of());
    }

    public static OrdenResponse from(Orden orden, List<OrdenItem> ordenItems, String proveedor,
            Map<Long, List<String>> categoriasPorLibro) {
        OrdenResponse r = from(orden);
        r.proveedor = proveedor;
        r.items = ordenItems.stream()
                .map(item -> OrdenItemResponse.from(item,
                        categoriasPorLibro.getOrDefault(item.getLibro().getId(), List.of())))
                .toList();
        return r;
    }

    public static OrdenResponse from(Orden orden) {
        OrdenResponse r = new OrdenResponse();
        r.id = orden.getId();
        r.fecha = orden.getFecha();
        r.venceEn = orden.getReservaHasta();
        r.provinciaDestino = orden.getProvinciaDestino();
        r.calleDestino = orden.getCalleDestino();
        r.ciudadDestino = orden.getCiudadDestino();
        r.cpDestino = orden.getCpDestino();
        r.subtotal = orden.getSubtotal();
        r.costoEnvio = orden.getCostoEnvio();
        r.total = orden.getTotal();
        r.estadoPago = orden.getEstadoPago() != null ? orden.getEstadoPago().name() : null;
        if (orden.getComprador() != null) {
            r.idComprador = orden.getComprador().getId();
            r.nombreComprador = orden.getComprador().getNombre();
            r.apellidoComprador = orden.getComprador().getApellido();
        }
        return r;
    }
}