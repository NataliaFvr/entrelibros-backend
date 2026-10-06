package com.uade.entrelibros.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ImagenCategoria {

    public ImagenCategoria() {
    }

    public ImagenCategoria(byte[] imagen, String tipoContenido, Categoria categoria) {
        this.imagen = imagen;
        this.tipoContenido = tipoContenido;
        this.categoria = categoria;
        this.activa = true;
        this.fechaActualizacion = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imagen;

    private String tipoContenido; // "image/jpeg" o "image/png"

    // Soft delete: al "borrar" la imagen se pone en false y la fila queda en la base.
    // Si se vuelve a subir una imagen para la misma categoria se reactiva.
    private Boolean activa = true;

    private LocalDateTime fechaActualizacion;

    // Una sola imagen por categoria (unique)
    @OneToOne
    @JoinColumn(name = "id_categoria", unique = true)
    private Categoria categoria;
}
