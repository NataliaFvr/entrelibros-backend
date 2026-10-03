package com.uade.entrelibros.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.Marcapagina;

@Repository
public interface MarcapaginaRepository extends JpaRepository<Marcapagina, Long> {

    // Solo libros que siguen visibles en el catalogo (ACTIVA + ACEPTADO)
    @Query("select m from Marcapagina m where m.usuario.id = :idUsuario "
            + "and m.libro.estadoPublicacion = com.uade.entrelibros.backend.entity.EstadoPublicacion.ACTIVA "
            + "and m.libro.estadoModeracion = com.uade.entrelibros.backend.entity.EstadoModeracion.ACEPTADO "
            + "order by m.fechaGuardado desc")
    Page<Marcapagina> findVisiblesByUsuarioId(Long idUsuario, Pageable pageable);

    Optional<Marcapagina> findByUsuarioIdAndLibroId(Long idUsuario, Long idLibro);

    boolean existsByUsuarioIdAndLibroId(Long idUsuario, Long idLibro);
}