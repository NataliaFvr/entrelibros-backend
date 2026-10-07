package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.EnvioItem;
import lombok.Data;

@Data
public class EnvioItemResponse {

    private Long id;
    private Double costo;
    private Long idOrdenVendedor;
    private String zona;

    public static EnvioItemResponse from(EnvioItem envioItem) {
        EnvioItemResponse r = new EnvioItemResponse();
        r.id = envioItem.getId();
        r.costo = envioItem.getCosto();
        r.idOrdenVendedor = envioItem.getOrdenVendedor().getId();
        r.zona = envioItem.getEnvio().getZona().name();
        return r;
    }
}
