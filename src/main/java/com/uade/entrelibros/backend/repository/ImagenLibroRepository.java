package com.uade.entrelibros.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.ImagenLibro;

@Repository
public interface ImagenLibroRepository extends JpaRepository<ImagenLibro, Long> {

    @Query(value = "select i from ImagenLibro i where i.libro.id = ?1 order by i.orden asc")
    List<ImagenLibro> findByLibroId(Long libroId);

    Optional<ImagenLibro> findFirstByLibroIdOrderByOrdenAsc(Long libroId);

    // Solo ids, NUNCA el LONGBLOB de la imagen: sirve para armar la URL de portada de toda una pagina de libros
    // con UNA consulta. Ordenado por libro y luego por orden: la primera fila de cada libro es su portada
    // (el mismo criterio que findFirstByLibroIdOrderByOrdenAsc, que usa el carrito).
    @Query("select i.libro.id, i.id from ImagenLibro i where i.libro.id in :idsLibros "
            + "order by i.libro.id asc, i.orden asc, i.id asc")
    List<Object[]> findIdsPorLibroIds(@Param("idsLibros") List<Long> idsLibros);
}