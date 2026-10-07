package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Direccion {

    public Direccion() {
    }

    public Direccion(Usuario usuario, String alias, String calle, String ciudad, String provincia, String cp) {
        this.usuario = usuario;
        this.alias = alias;
        this.calle = calle;
        this.ciudad = ciudad;
        this.provincia = provincia;
        this.cp = cp;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean principal;

    private String alias;
    private String calle;
    private String ciudad;
    private String provincia;
    private String cp;
}