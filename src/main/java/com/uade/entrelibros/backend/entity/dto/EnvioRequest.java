package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.ZonaEnvio;
import lombok.Data;

@Data
public class EnvioRequest {
    private ZonaEnvio zona;
    private Double costoFijo;
}
