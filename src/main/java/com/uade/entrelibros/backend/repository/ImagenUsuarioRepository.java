package com.uade.entrelibros.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.ImagenUsuario;

@Repository
public interface ImagenUsuarioRepository extends JpaRepository<ImagenUsuario, Long> {

    // La fila del usuario, este activa o no (se usa para reactivarla al subir una nueva)
    @Query(value = "select i from ImagenUsuario i where i.usuario.id = ?1")
    Optional<ImagenUsuario> findByUsuarioId(Long usuarioId);

    @Query(value = "select i from ImagenUsuario i where i.usuario.id = ?1 and i.activa = true")
    Optional<ImagenUsuario> findActivaByUsuarioId(Long usuarioId);
}
