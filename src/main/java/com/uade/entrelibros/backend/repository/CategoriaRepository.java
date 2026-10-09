package com.uade.entrelibros.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.uade.entrelibros.backend.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query(value = "select c from Categoria c where c.nombre = ?1")
    Categoria findByNombre(String nombre);

    List<Categoria> findByActivaTrueOrderByNombreAsc();

    // Activas e inactivas: para el panel del admin
    List<Categoria> findAllByOrderByNombreAsc();
}