package com.uade.entrelibros.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.CarritoItem;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    @Query(value = "select ci from CarritoItem ci where ci.carrito.id = ?1")
    List<CarritoItem> findByCarritoId(Long idCarrito);

    @Query(value = "select ci from CarritoItem ci where ci.libro.vendedor.id = ?1")
    List<CarritoItem> findByVendedorId(Long idVendedor);

    @Query(value = "select ci from CarritoItem ci where ci.carrito.id = ?1 and ci.libro.id = ?2")
    Optional<CarritoItem> findByCarritoIdAndLibroId(Long idCarrito, Long idLibro);
}