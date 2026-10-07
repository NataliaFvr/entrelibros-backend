package com.uade.entrelibros.backend.entity.dto;

import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LibroRequest {
    public interface Crear {}
    public interface Actualizar {}

    @NotBlank(message = "El título es obligatorio", groups = Crear.class)
    @Pattern(regexp = ".*\\S.*", message = "El título no puede estar vacío", groups = Actualizar.class)
    private String titulo;
    private String autor;
    private String editorial;

    @NotNull(message = "El año es obligatorio", groups = Crear.class)
    @Min(value = 1900, message = "El año debe ser mayor o igual a 1900",
            groups = {Crear.class, Actualizar.class})
    private Integer anio;
    private String idioma;
    private String estadoLibro;

    @NotNull(message = "El precio es obligatorio", groups = Crear.class)
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0",
            groups = {Crear.class, Actualizar.class})
    private Double precio;

    @NotNull(message = "El descuento es obligatorio", groups = Crear.class)
    @DecimalMin(value = "0.0", message = "El descuento no puede ser menor a 0",
            groups = {Crear.class, Actualizar.class})
    @DecimalMax(value = "100.0", message = "El descuento no puede ser mayor a 100",
            groups = {Crear.class, Actualizar.class})
    private Double descuentoPct;

    @NotNull(message = "El stock es obligatorio", groups = Crear.class)
    @Min(value = 1, message = "El stock debe ser de al menos 1",
            groups = {Crear.class, Actualizar.class})
    private Integer stock;
    private String descripcion;
    private Long idVendedor;
    private List<Long> idCategorias;
}