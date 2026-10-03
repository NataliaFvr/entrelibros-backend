package com.uade.entrelibros.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.EstadoSolicitud;
import com.uade.entrelibros.backend.entity.SolicitudVendedor;

@Repository
public interface SolicitudVendedorRepository extends JpaRepository<SolicitudVendedor, Long> {

    boolean existsByUsuarioIdAndEstado(Long idUsuario, EstadoSolicitud estado);

    // Las mas viejas primero: es una cola de trabajo para el admin
    Page<SolicitudVendedor> findByEstadoOrderByFechaSolicitudAsc(EstadoSolicitud estado, Pageable pageable);

    Page<SolicitudVendedor> findByUsuarioIdOrderByFechaSolicitudDesc(Long idUsuario, Pageable pageable);

    // Candado para que dos admins no resuelvan la misma solicitud a la vez
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SolicitudVendedor s where s.id = :id")
    Optional<SolicitudVendedor> findByIdConCandado(Long id);
}