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

    // Listado por estado de moderacion (solo para el admin): NO pasa por visibles(),
    // asi el catalogo del comprador sigue sin mostrar los EN_REVISION.
    // Exige ACTIVA para que la cola ignore los libros que el vendedor ya dio de baja.
    Page<Libro> findByEstadoModeracionAndEstadoPublicacion(
            com.uade.entrelibros.backend.entity.EstadoModeracion estadoModeracion,
            com.uade.entrelibros.backend.entity.EstadoPublicacion estadoPublicacion,
            Pageable pageable);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Libro l where l.id = :id")
    java.util.Optional<Libro> findByIdConCandado(Long id);
}