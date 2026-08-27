package com.uade.entrelibros.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.Carrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    @Query(value = "select c from Carrito c where c.usuario.id = ?1")
    Carrito findByUsuarioId(Long idUsuario);
}