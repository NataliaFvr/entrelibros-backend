package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.ZonaEnvio;
import lombok.Data;

@Data
public class EnvioItemRequest {
    private Long idOrdenVendedor;
    private ZonaEnvio zona;
}
