package com.uade.entrelibros.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.EstadoPago;
import com.uade.entrelibros.backend.entity.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    @Query(value = "select p from Pago p where p.orden.id = ?1")
    List<Pago> findByOrdenId(Long idOrden);

    List<Pago> findByOrdenIdIn(List<Long> idsOrden);

    @Query("select count(p) from Pago p where p.resultado = :resultado")
    long contarPorResultado(@Param("resultado") EstadoPago resultado);

    // Sin pagos con ese resultado devuelve 0.0 (no null)
    @Query("select coalesce(sum(p.orden.total), 0.0) from Pago p where p.resultado = :resultado")
    Double sumarTotalPorResultado(@Param("resultado") EstadoPago resultado);
}