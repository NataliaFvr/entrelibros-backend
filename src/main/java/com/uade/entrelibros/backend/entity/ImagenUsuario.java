package com.uade.entrelibros.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ImagenUsuario {

    public ImagenUsuario() {
    }

    public ImagenUsuario(byte[] imagen, String tipoContenido, Usuario usuario) {
        this.imagen = imagen;
        this.tipoContenido = tipoContenido;
        this.usuario = usuario;
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

    // Soft delete: al quitar la foto se pone en false y la fila queda en la base.
    // Si el usuario sube otra foto se reactiva.
    private Boolean activa = true;

    private LocalDateTime fechaActualizacion;

    // Una sola foto por usuario (unique)
    @OneToOne
    @JoinColumn(name = "id_usuario", unique = true)
    private Usuario usuario;
}
