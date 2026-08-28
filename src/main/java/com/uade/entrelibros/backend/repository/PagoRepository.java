package com.uade.entrelibros.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    @Query(value = "select p from Pago p where p.orden.id = ?1")
    List<Pago> findByOrdenId(Long idOrden);
}
