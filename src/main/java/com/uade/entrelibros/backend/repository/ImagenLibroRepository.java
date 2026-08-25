package com.uade.entrelibros.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.ImagenLibro;

@Repository
public interface ImagenLibroRepository extends JpaRepository<ImagenLibro, Long> {

    @Query(value = "select i from ImagenLibro i where i.libro.id = ?1 order by i.orden asc")
    List<ImagenLibro> findByLibroId(Long libroId);
}