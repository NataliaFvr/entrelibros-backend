package com.uade.entrelibros.backend.service;

import com.uade.entrelibros.backend.entity.ConfiguracionCargo;
import com.uade.entrelibros.backend.entity.Orden;

public interface CargoServicioService {

    ConfiguracionCargo obtener();

    ConfiguracionCargo actualizar(Double porcentaje);

    double porcentajeVigente();


    static double calcular(double subtotalLibros, double porcentaje) {
        if (porcentaje <= 0 || subtotalLibros <= 0) {
            return 0.0;
        }
        return EnvioPolicy.redondear(subtotalLibros * porcentaje / 100.0);
    }


    static double cargoDe(Orden orden) {
        double subtotal = orden.getSubtotal() != null ? orden.getSubtotal() : 0.0;
        double envio = orden.getCostoEnvio() != null ? orden.getCostoEnvio() : 0.0;
        double total = orden.getTotal() != null ? orden.getTotal() : 0.0;
        return Math.max(0.0, EnvioPolicy.redondear(total - subtotal - envio));
    }
}