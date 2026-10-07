package com.uade.entrelibros.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.LibroCategoria;

@Repository
public interface LibroCategoriaRepository extends JpaRepository<LibroCategoria, Long> {

    @Query(value = "select lc from LibroCategoria lc where lc.libro.id = ?1")
    List<LibroCategoria> findByLibroId(Long libroId);
}