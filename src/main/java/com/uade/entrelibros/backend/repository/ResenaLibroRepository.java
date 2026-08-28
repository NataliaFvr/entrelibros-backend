package com.uade.entrelibros.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.ResenaLibro;

@Repository
public interface ResenaLibroRepository extends JpaRepository<ResenaLibro, Long> {

    @Query(value = "select r from ResenaLibro r where r.ordenItem.id = ?1")
    List<ResenaLibro> findByOrdenItemId(Long idOrdenItem);

    @Query(value = "select r from ResenaLibro r where r.ordenItem.libro.id = ?1")
    List<ResenaLibro> findByLibroId(Long idLibro);
}
