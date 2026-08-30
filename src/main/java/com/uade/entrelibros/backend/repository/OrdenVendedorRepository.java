package com.uade.entrelibros.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.OrdenVendedor;

@Repository
public interface OrdenVendedorRepository extends JpaRepository<OrdenVendedor, Long> {

    @Query(value = "select ov from OrdenVendedor ov where ov.orden.id = ?1")
    List<OrdenVendedor> findByOrdenId(Long idOrden);

    @Query(value = "select ov from OrdenVendedor ov where ov.vendedor.id = ?1")
    List<OrdenVendedor> findByVendedorId(Long idVendedor);

    Optional<OrdenVendedor> findByOrdenIdAndVendedorId(Long idOrden, Long idVendedor);
}
