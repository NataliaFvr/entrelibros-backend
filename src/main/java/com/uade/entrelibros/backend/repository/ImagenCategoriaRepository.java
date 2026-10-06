package com.uade.entrelibros.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.ImagenCategoria;

@Repository
public interface ImagenCategoriaRepository extends JpaRepository<ImagenCategoria, Long> {

    // La fila de la categoria, este activa o no (se usa para reactivarla al subir una nueva)
    @Query(value = "select i from ImagenCategoria i where i.categoria.id = ?1")
    Optional<ImagenCategoria> findByCategoriaId(Long categoriaId);

    @Query(value = "select i from ImagenCategoria i where i.categoria.id = ?1 and i.activa = true")
    Optional<ImagenCategoria> findActivaByCategoriaId(Long categoriaId);

    // Solo ids: sirve para armar tieneImagen de toda la lista sin traer los bytes de las imagenes
    @Query(value = "select i.categoria.id from ImagenCategoria i where i.activa = true")
    List<Long> findIdsCategoriasConImagenActiva();
}
