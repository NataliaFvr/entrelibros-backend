package com.uade.entrelibros.backend.entity.dto;

<<<<<<< HEAD
import java.util.List;

=======
import com.uade.entrelibros.backend.entity.EnvioItem;
>>>>>>> 2265f6276d9c8b01ecdf2c1c0825b9275fd72f2f
import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.Pago;
import lombok.Data;

@Data
public class PagoResponse {

    private Long id;
    private String proveedor;
    private String resultado;
    private Long idOrden;
    private Double totalOrden;
<<<<<<< HEAD
    private List<Long> idOrdenItems;
=======
    private List<Long> idsOrdenItem;
    private List<Long> idsEnvioItem;
>>>>>>> 2265f6276d9c8b01ecdf2c1c0825b9275fd72f2f

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
<<<<<<< HEAD
        r.idOrdenItems = items.stream()
                .map(OrdenItem::getId)
                .toList();
=======
        r.idsOrdenItem = ordenItems.stream().map(OrdenItem::getId).toList();
        r.idsEnvioItem = envioItems.stream().map(EnvioItem::getId).toList();
>>>>>>> 2265f6276d9c8b01ecdf2c1c0825b9275fd72f2f
        return r;
    }
}
