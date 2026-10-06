package com.uade.entrelibros.backend.entity.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class EstadisticasVendedorResponse {

    private Double ingresosTotales;
    private Long unidadesVendidas;
    private Integer cantidadVentas;
    private Double promedioPorVenta;
    private List<Desglose> ventasPorCategoria;
    private List<Desglose> ventasPorEstado; // NUEVO / USADO

    @Data
    @AllArgsConstructor
    public static class Desglose {
        private String nombre;
        private Long unidades;
        private Double ingresos;
    }
}