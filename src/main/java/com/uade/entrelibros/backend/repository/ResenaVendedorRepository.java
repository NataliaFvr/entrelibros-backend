package com.uade.entrelibros.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.ResenaVendedor;

@Repository
public interface ResenaVendedorRepository extends JpaRepository<ResenaVendedor, Long> {

    @Query(value = "select r from ResenaVendedor r where r.pago.id = ?1 and r.vendedor.id = ?2 and r.comprador.id = ?3")
    List<ResenaVendedor> findByPagoIdAndVendedorIdAndCompradorId(Long idPago, Long idVendedor, Long idComprador);

    @Query(value = "select r from ResenaVendedor r where r.vendedor.id = ?1")
    List<ResenaVendedor> findByVendedorId(Long idVendedor);
}
