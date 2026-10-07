package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.OrdenVendedor;
import com.uade.entrelibros.backend.entity.OrdenItem;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class OrdenVendedorResponse {

    private Long id;
    private String estado;
    private Long idOrden;
    private Long idVendedor;
    private String nombreVendedor;
    private LocalDateTime fecha;
    private Long idComprador;
    private String nombreComprador;
    private String apellidoComprador;
    private String estadoPago;
    private String proveedor;
    private List<OrdenItemResponse> items;

    public static OrdenVendedorResponse from(OrdenVendedor ov) {
        return from(ov, List.of(), null);
    }

    public static OrdenVendedorResponse from(OrdenVendedor ov, List<OrdenItem> items, String proveedor) {
        return from(ov, items, proveedor, Map.of());
    }

    public static OrdenVendedorResponse from(OrdenVendedor ov, List<OrdenItem> items, String proveedor,
            Map<Long, List<String>> categoriasPorLibro) {
        OrdenVendedorResponse r = new OrdenVendedorResponse();
        r.id = ov.getId();
        r.estado = ov.getEstado() != null ? ov.getEstado().name() : null;
        if (ov.getOrden() != null) {
            r.idOrden = ov.getOrden().getId();
            r.fecha = ov.getOrden().getFecha();
            r.estadoPago = ov.getOrden().getEstadoPago() != null
                    ? ov.getOrden().getEstadoPago().name() : null;
            r.proveedor = proveedor;
            if (ov.getOrden().getComprador() != null) {
                r.idComprador = ov.getOrden().getComprador().getId();
                r.nombreComprador = ov.getOrden().getComprador().getNombre();
                r.apellidoComprador = ov.getOrden().getComprador().getApellido();
            }
        }
        if (ov.getVendedor() != null) {
            r.idVendedor = ov.getVendedor().getId();
            r.nombreVendedor = ov.getVendedor().getNombre();
        }
        r.items = items.stream()
                .map(item -> OrdenItemResponse.from(item,
                        categoriasPorLibro.getOrDefault(item.getLibro().getId(), List.of())))
                .toList();
        return r;
    }
}
