package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.Envio;
import lombok.Data;

@Data
public class EnvioResponse {

    private Long id;
    private String zona;
    private Double costoFijo;

    public static EnvioResponse from(Envio envio) {
        EnvioResponse r = new EnvioResponse();
        r.id = envio.getId();
        r.zona = envio.getZona().name();
        r.costoFijo = envio.getCostoFijo();
        return r;
    }
}
