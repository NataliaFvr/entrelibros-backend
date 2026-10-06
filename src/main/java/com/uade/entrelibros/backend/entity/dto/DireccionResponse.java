package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.Direccion;
import lombok.Data;

@Data
public class DireccionResponse {

    private Long id;
    private String alias;
    private String calle;
    private String ciudad;
    private String provincia;
    private String cp;

    public static DireccionResponse from(Direccion d) {
        DireccionResponse r = new DireccionResponse();
        r.id = d.getId();
        r.alias = d.getAlias();
        r.calle = d.getCalle();
        r.ciudad = d.getCiudad();
        r.provincia = d.getProvincia();
        r.cp = d.getCp();
        return r;
    }
}