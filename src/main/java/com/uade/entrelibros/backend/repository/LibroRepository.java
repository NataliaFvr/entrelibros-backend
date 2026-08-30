package com.uade.entrelibros.backend.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.uade.entrelibros.backend.entity.Libro;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
     @Query("select l from Libro l where l.estadoPublicacion = com.uade.entrelibros.backend.entity.EstadoPublicacion.ACTIVA")
    List<Libro> findVisibles();
    @Query(value = "select l from Libro l where l.vendedor.id = ?1")
    List<Libro> findByVendedorId(Long idVendedor);
}