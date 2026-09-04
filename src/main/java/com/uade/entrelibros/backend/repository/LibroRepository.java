package com.uade.entrelibros.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

import com.uade.entrelibros.backend.entity.Libro;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long>, JpaSpecificationExecutor<Libro> {

    @Query("select l from Libro l where l.estadoPublicacion = com.uade.entrelibros.backend.entity.EstadoPublicacion.ACTIVA")
    Page<Libro> findVisibles(Pageable pageable);

    @Query(value = "select l from Libro l where l.vendedor.id = ?1")
    List<Libro> findByVendedorId(Long idVendedor);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Libro l where l.id = :id")
    java.util.Optional<Libro> findByIdConCandado(Long id);
}