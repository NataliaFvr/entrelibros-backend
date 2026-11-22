package com.uade.entrelibros.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    public RefreshToken() {
    }

    public RefreshToken(Usuario usuario, String tokenId, Instant expiresAt) {
        this.usuario = usuario;
        this.tokenId = tokenId;
        this.expiresAt = expiresAt;
        this.revocado = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tokenId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Boolean revocado = false;
}
