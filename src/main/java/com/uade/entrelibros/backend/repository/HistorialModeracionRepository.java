package com.uade.entrelibros.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.HistorialModeracion;
import com.uade.entrelibros.backend.entity.EstadoModeracion;
import java.util.List;

@Repository
public interface HistorialModeracionRepository extends JpaRepository<HistorialModeracion, Long> {

    Page<HistorialModeracion> findByLibroIdOrderByFechaDesc(Long idLibro, Pageable pageable);

    Page<HistorialModeracion> findAllByOrderByFechaDesc(Pageable pageable);

    List<HistorialModeracion> findByLibroIdInAndEstadoNuevoOrderByFechaDesc(
            List<Long> idsLibros, EstadoModeracion estadoNuevo);
}