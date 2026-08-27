package com.uade.entrelibros.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.Orden;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

    @Query(value = "select o from Orden o where o.comprador.id = ?1")
    List<Orden> findByCompradorId(Long idComprador);
}