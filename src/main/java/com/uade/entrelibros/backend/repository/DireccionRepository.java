package com.uade.entrelibros.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.Direccion;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {

    @Query("select d from Direccion d where d.usuario.id = ?1 order by d.id")
    List<Direccion> findByUsuarioId(Long idUsuario);

    Optional<Direccion> findByIdAndUsuarioId(Long id, Long idUsuario);
}