package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Usuario {

    public Usuario() {
    }

    public Usuario(String nombreUsuario, String email, String contrasenaHash, String nombre, String apellido) {
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.contrasenaHash = contrasenaHash;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = Rol.NO_ADMIN;
        this.esVendedor = false;
        this.estado = EstadoUsuario.ACTIVO;
        this.fechaRegistro = LocalDate.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombreUsuario;

    @Column(unique = true)
    private String email;

    private String contrasenaHash;
    private String nombre;
    private String apellido;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    private boolean esVendedor;

    @Enumerated(EnumType.STRING)
    private EstadoUsuario estado;

    private LocalDate fechaRegistro;

    public Long getId() {
        return id;
    }
}