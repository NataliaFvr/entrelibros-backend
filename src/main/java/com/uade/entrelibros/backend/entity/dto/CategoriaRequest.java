package com.uade.entrelibros.backend.entity.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoriaRequest {

    @NotBlank(message = "Escribí el nombre de la categoría.")
    @Size(min = 2, max = 40, message = "Entre 2 y 40 caracteres.")
    @Pattern(regexp = "^[\\p{L}\\p{N}][\\p{L}\\p{N}\\s&,.'\\u2019/-]*$", message = "Usá letras, números, espacios o & , . - / solamente.")
    private String nombre;

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.strip().replaceAll("\\s+", " ");
    }
}