package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
public class Usuario implements UserDetails {

    public Usuario() {
    }

    public Usuario(String nombreUsuario, String email, String contrasenaHash, String nombre, String apellido) {
        this(nombreUsuario, email, contrasenaHash, nombre, apellido, Rol.COMPRADOR);
    }

    public Usuario(String nombreUsuario, String email, String contrasenaHash, String nombre, String apellido,
            Rol rol) {
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.contrasenaHash = contrasenaHash;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol != null ? rol : Rol.COMPRADOR;
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

    @Enumerated(EnumType.STRING)
    private EstadoUsuario estado;

    private LocalDate fechaRegistro;

    public Long getId() {
        return id;
    }

  
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }

    @Override
    public String getPassword() {
        return contrasenaHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return estado == EstadoUsuario.ACTIVO;
    }
}