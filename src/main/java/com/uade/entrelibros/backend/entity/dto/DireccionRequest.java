package com.uade.entrelibros.backend.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DireccionRequest {
    @NotBlank
    private String alias;
    @NotBlank
    private String calle;
    @NotBlank
    private String ciudad;
    @NotBlank
    private String provincia;
    @NotBlank
    private String cp;
}