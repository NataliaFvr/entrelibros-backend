package com.uade.entrelibros.backend.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendedorOptionResponse {
    private Long id;
    private String nombre;
}