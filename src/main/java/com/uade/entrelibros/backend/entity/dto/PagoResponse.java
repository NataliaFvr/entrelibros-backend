package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.EnvioItem;
import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.Pago;
import lombok.Data;

import java.util.List;

@Data
public class PagoResponse {

    private Long id;
    private String proveedor;
    private String resultado;
    private Long idOrden;
    private Double totalOrden;
    private List<Long> idsOrdenItem;
    private List<Long> idsEnvioItem;

    public static PagoResponse from(Pago pago) {
        PagoResponse r = new PagoResponse();
        r.id = pago.getId();
        r.proveedor = pago.getProveedor();
        r.resultado = pago.getResultado().name();
        r.idOrden = pago.getOrden().getId();
        r.totalOrden = pago.getOrden().getTotal();
        return r;
    }

    public static PagoResponse from(Pago pago, List<OrdenItem> ordenItems, List<EnvioItem> envioItems) {
        PagoResponse r = from(pago);
        r.idsOrdenItem = ordenItems.stream().map(OrdenItem::getId).toList();
        r.idsEnvioItem = envioItems.stream().map(EnvioItem::getId).toList();
        return r;
    }
}