package com.uade.entrelibros.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.OrdenItem;

@Repository
public interface OrdenItemRepository extends JpaRepository<OrdenItem, Long> {

    @Query(value = "select oi from OrdenItem oi where oi.orden.id = ?1")
    List<OrdenItem> findByOrdenId(Long idOrden);

    List<OrdenItem> findByOrdenIdAndVendedorId(Long idOrden, Long idVendedor);

    List<OrdenItem> findByOrdenIdInAndVendedorId(List<Long> idsOrden, Long idVendedor);

    java.util.Optional<OrdenItem> findFirstByOrdenIdAndVendedorIdOrderByIdAsc(
            Long idOrden, Long idVendedor);

    @Query("select oi from OrdenItem oi where oi.vendedor.id = ?1 "
            + "and oi.orden.estadoPago = com.uade.entrelibros.backend.entity.EstadoPago.SIMULADO_APROBADO "
            + "and exists (select ov.id from OrdenVendedor ov where ov.orden = oi.orden and ov.vendedor = oi.vendedor "
            + "and ov.estado = com.uade.entrelibros.backend.entity.EstadoOrdenVendedor.ACTIVA)")
    List<OrdenItem> findVentasConfirmadasByVendedorId(Long idVendedor);
}
