package com.uade.entrelibros.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Usuario;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long>, JpaSpecificationExecutor<Libro> {

    @Query("select l from Libro l where l.estadoPublicacion = com.uade.entrelibros.backend.entity.EstadoPublicacion.ACTIVA")
    Page<Libro> findVisibles(Pageable pageable);

    @Query(value = "select l from Libro l where l.vendedor.id = ?1")
    List<Libro> findByVendedorId(Long idVendedor);

    // Listado por estado de moderacion (solo para el admin): NO pasa por visibles(),
    // asi el catalogo del comprador sigue sin mostrar los EN_REVISION.
    // Exige ACTIVA para que la cola ignore los libros que el vendedor ya dio de baja.
    Page<Libro> findByEstadoModeracionAndEstadoPublicacion(
            com.uade.entrelibros.backend.entity.EstadoModeracion estadoModeracion,
            com.uade.entrelibros.backend.entity.EstadoPublicacion estadoPublicacion,
            Pageable pageable);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Libro l where l.id = :id")
    java.util.Optional<Libro> findByIdConCandado(Long id);

    // Valores únicos entre libros visibles, para poblar los <select> de filtro del catálogo.
    // Siempre sobre libros ACTIVA + ACEPTADO: no tiene sentido ofrecer como filtro un valor
    // que solo existe en libros que el catálogo público no va a mostrar de todos modos.
    @Query("select distinct l.editorial from Libro l where l.estadoPublicacion = com.uade.entrelibros.backend.entity.EstadoPublicacion.ACTIVA and l.estadoModeracion = com.uade.entrelibros.backend.entity.EstadoModeracion.ACEPTADO order by l.editorial")
    List<String> findEditorialesDistintas();

    @Query("select distinct l.autor from Libro l where l.estadoPublicacion = com.uade.entrelibros.backend.entity.EstadoPublicacion.ACTIVA and l.estadoModeracion = com.uade.entrelibros.backend.entity.EstadoModeracion.ACEPTADO order by l.autor")
    List<String> findAutoresDistintos();

    @Query("select distinct l.idioma from Libro l where l.estadoPublicacion = com.uade.entrelibros.backend.entity.EstadoPublicacion.ACTIVA and l.estadoModeracion = com.uade.entrelibros.backend.entity.EstadoModeracion.ACEPTADO order by l.idioma")
    List<String> findIdiomasDistintos();

    @Query("select distinct l.vendedor from Libro l where l.estadoPublicacion = com.uade.entrelibros.backend.entity.EstadoPublicacion.ACTIVA and l.estadoModeracion = com.uade.entrelibros.backend.entity.EstadoModeracion.ACEPTADO")
    List<Usuario> findVendedoresConLibrosVisibles();
}