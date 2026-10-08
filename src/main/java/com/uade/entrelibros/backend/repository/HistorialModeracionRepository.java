package com.uade.entrelibros.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.HistorialModeracion;
import com.uade.entrelibros.backend.entity.EstadoModeracion;
import java.util.List;

@Repository
public interface HistorialModeracionRepository extends JpaRepository<HistorialModeracion, Long> {

    Page<HistorialModeracion> findByLibroIdOrderByFechaDesc(Long idLibro, Pageable pageable);

    Page<HistorialModeracion> findAllByOrderByFechaDesc(Pageable pageable);

    List<HistorialModeracion> findByLibroIdInAndEstadoNuevoOrderByFechaDesc(
            List<Long> idsLibros, EstadoModeracion estadoNuevo);

    // Motivo del ULTIMO rechazo de cada libro, para toda una pagina en UNA consulta y sin cargar entidades
    // (el metodo anterior trae cada HistorialModeracion con su libro y moderador EAGER = selects extra).
    // Filas [idLibro (Long), comentario (String)]. "Ultimo" = mayor id (IDENTITY, orden cronologico).
    @Query("select h.libro.id, h.comentario from HistorialModeracion h "
            + "where h.libro.id in :idsLibros "
            + "and h.estadoNuevo = com.uade.entrelibros.backend.entity.EstadoModeracion.RECHAZADO "
            + "and h.id = (select max(h2.id) from HistorialModeracion h2 "
            + "            where h2.libro.id = h.libro.id "
            + "            and h2.estadoNuevo = com.uade.entrelibros.backend.entity.EstadoModeracion.RECHAZADO)")
    List<Object[]> findUltimoMotivoRechazoPorLibroIds(@Param("idsLibros") List<Long> idsLibros);
}