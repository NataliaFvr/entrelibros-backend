package com.uade.entrelibros.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.ResenaVendedor;

@Repository
public interface ResenaVendedorRepository extends JpaRepository<ResenaVendedor, Long> {

    @Query(value = "select r from ResenaVendedor r where r.envioItem.id = ?1 and r.comprador.id = ?2")
    List<ResenaVendedor> findByEnvioItemIdAndCompradorId(Long idEnvioItem, Long idComprador);

    @Query(value = "select r from ResenaVendedor r where r.envioItem.ordenVendedor.vendedor.id = ?1")
    List<ResenaVendedor> findByVendedorId(Long idVendedor);
}
