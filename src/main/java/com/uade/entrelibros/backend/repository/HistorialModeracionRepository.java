package com.uade.entrelibros.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.HistorialModeracion;

@Repository
public interface HistorialModeracionRepository extends JpaRepository<HistorialModeracion, Long> {

    Page<HistorialModeracion> findByLibroIdOrderByFechaDesc(Long idLibro, Pageable pageable);

    Page<HistorialModeracion> findAllByOrderByFechaDesc(Pageable pageable);
}