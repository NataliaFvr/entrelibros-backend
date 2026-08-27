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
}