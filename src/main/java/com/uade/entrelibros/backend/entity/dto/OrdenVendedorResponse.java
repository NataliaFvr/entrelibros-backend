package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.OrdenVendedor;
import lombok.Data;

@Data
public class OrdenVendedorResponse {

    private Long id;
    private String estado;
    private Long idOrden;
    private Long idVendedor;
    private String nombreVendedor;

    public static OrdenVendedorResponse from(OrdenVendedor ov) {
        OrdenVendedorResponse r = new OrdenVendedorResponse();
        r.id = ov.getId();
        r.estado = ov.getEstado() != null ? ov.getEstado().name() : null;
        if (ov.getOrden() != null) {
            r.idOrden = ov.getOrden().getId();
        }
        if (ov.getVendedor() != null) {
            r.idVendedor = ov.getVendedor().getId();
            r.nombreVendedor = ov.getVendedor().getNombre();
        }
        return r;
    }
}
