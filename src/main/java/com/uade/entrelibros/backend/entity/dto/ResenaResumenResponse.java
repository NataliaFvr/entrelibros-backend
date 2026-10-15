package com.uade.entrelibros.backend.entity.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ResenaResumenResponse {

    private double promedio;
    private long cantidad;
    private Map<Integer, Long> distribucion;

    public static ResenaResumenResponse de(List<Integer> puntuaciones) {
        Map<Integer, Long> distribucion = new LinkedHashMap<>();
        for (int estrellas = 1; estrellas <= 5; estrellas++) {
            distribucion.put(estrellas, 0L);
        }
        long cantidad = 0;
        long suma = 0;
        for (Integer puntuacion : puntuaciones) {
            if (puntuacion == null || puntuacion < 1 || puntuacion > 5) {
                continue;
            }
            distribucion.merge(puntuacion, 1L, Long::sum);
            cantidad++;
            suma += puntuacion;
        }
        ResenaResumenResponse r = new ResenaResumenResponse();
        r.cantidad = cantidad;
        r.promedio = cantidad == 0 ? 0.0 : Math.round(suma * 100.0 / cantidad) / 100.0;
        r.distribucion = distribucion;
        return r;
    }
}