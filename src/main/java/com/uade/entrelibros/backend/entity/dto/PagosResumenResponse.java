package com.uade.entrelibros.backend.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagosResumenResponse {
    // Suma del total de las ordenes con pago aprobado
    private Double totalAprobado;
    private long aprobados;
    private long rechazados;
    // Todos los pagos registrados
    private long total;
}