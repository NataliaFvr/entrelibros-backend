package com.uade.entrelibros.backend.entity.dto;

import java.util.List;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LibroRequest {

    public interface Crear {}
    public interface Actualizar {}

    @NotBlank(message = "Ingresá el título.", groups = Crear.class)
    @Pattern(regexp = ".*\\S.*", message = "Ingresá el título.", groups = Actualizar.class)
    @Size(max = 150, message = "Máximo 150 caracteres.", groups = {Crear.class, Actualizar.class})
    private String titulo;

    @NotBlank(message = "Ingresá el autor.", groups = Crear.class)
    @Pattern(regexp = ".*\\S.*", message = "Ingresá el autor.", groups = Actualizar.class)
    @Size(max = 100, message = "Máximo 100 caracteres.", groups = {Crear.class, Actualizar.class})
    private String autor;

    @NotBlank(message = "Ingresá la editorial.", groups = Crear.class)
    @Pattern(regexp = ".*\\S.*", message = "Ingresá la editorial.", groups = Actualizar.class)
    @Size(max = 100, message = "Máximo 100 caracteres.", groups = {Crear.class, Actualizar.class})
    private String editorial;

    @NotNull(message = "Ingresá el año de edición.", groups = Crear.class)
    @Min(value = 1900, message = "El año debe ser 1900 o posterior.",
            groups = {Crear.class, Actualizar.class})
    private Integer anio;

    @NotBlank(message = "Elegí un idioma.", groups = Crear.class)
    @Pattern(regexp = ".*\\S.*", message = "Elegí un idioma.", groups = Actualizar.class)
    @Size(max = 50, message = "Máximo 50 caracteres.", groups = {Crear.class, Actualizar.class})
    private String idioma;

    @NotNull(message = "Elegí si el libro es nuevo o usado.", groups = Crear.class)
    private String estadoLibro;

    @NotNull(message = "Ingresá el precio.", groups = Crear.class)
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0.",
            groups = {Crear.class, Actualizar.class})
    private Double precio;

    @NotNull(message = "Ingresá el descuento (0 si no tiene).", groups = Crear.class)
    @DecimalMin(value = "0.0", message = "El descuento debe estar entre 0 y 100.",
            groups = {Crear.class, Actualizar.class})
    @DecimalMax(value = "100.0", message = "El descuento debe estar entre 0 y 100.",
            groups = {Crear.class, Actualizar.class})
    private Double descuentoPct;

    // Sin @NotNull: en un libro USADO el stock siempre es 1 y el back lo fija solo (los nuevos lo exigen en el service)
    @Min(value = 1, message = "El stock debe ser al menos 1.", groups = {Crear.class, Actualizar.class})
    private Integer stock;

    @Size(max = 1000, message = "Máximo 1000 caracteres.", groups = {Crear.class, Actualizar.class})
    private String descripcion;

    private List<Long> idCategorias;

    public void setTitulo(String titulo) {
        this.titulo = titulo == null ? null : titulo.strip();
    }

    public void setAutor(String autor) {
        this.autor = autor == null ? null : autor.strip();
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial == null ? null : editorial.strip();
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma == null ? null : idioma.strip();
    }
}
