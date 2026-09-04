package com.uade.entrelibros.backend.repository;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.Orden;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

    @Query(value = "select o from Orden o where o.comprador.id = ?1")
    List<Orden> findByCompradorId(Long idComprador);

    List<Orden> findByEstadoPagoAndReservaHastaLessThanEqual(
            com.uade.entrelibros.backend.entity.EstadoPago estadoPago, LocalDateTime fecha);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Orden o where o.id = :id")
    java.util.Optional<Orden> findByIdConCandado(Long id);
}
