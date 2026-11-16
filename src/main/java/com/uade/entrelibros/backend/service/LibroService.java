package com.uade.entrelibros.backend.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.uade.entrelibros.backend.entity.dto.LibroFiltroRequest;
import com.uade.entrelibros.backend.entity.EstadoModeracion;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.entity.dto.LibroRequest;
import com.uade.entrelibros.backend.entity.dto.FiltrosDisponiblesResponse;
import com.uade.entrelibros.backend.entity.dto.LibroMioResponse;
import com.uade.entrelibros.backend.entity.dto.LibroResponse;

public interface LibroService {
    Page<Libro> getLibros(PageRequest pageRequest);

    Page<Libro> buscarLibros(LibroFiltroRequest filtro, Pageable pageable);

    Libro getLibroById(Long libroId, Usuario usuario) throws LibroNoEncontradoException;

    Libro createLibro(LibroRequest request, Usuario vendedor)
            throws CategoriaNoEncontradaException, RolInvalidoException;

    Libro updateLibro(Long libroId, LibroRequest request, Usuario vendedor)
            throws LibroNoEncontradoException, CategoriaNoEncontradaException, RolInvalidoException,
            AccionNoPermitidaException;

    Libro darDeBajaLibro(Long libroId, Usuario vendedor)
            throws LibroNoEncontradoException, RolInvalidoException, AccionNoPermitidaException;

    Libro reactivarLibro(Long libroId, Usuario vendedor)
            throws LibroNoEncontradoException, RolInvalidoException, AccionNoPermitidaException;

    Libro moderarLibro(Long libroId, EstadoModeracion estadoModeracion, String comentario, Usuario moderador)
        throws LibroNoEncontradoException;

    Page<Libro> getLibrosPorEstadoModeracion(EstadoModeracion estado, PageRequest pageRequest);

    FiltrosDisponiblesResponse getFiltrosDisponibles();

    Page<LibroMioResponse> getLibrosMios(Usuario vendedor, EstadoModeracion estadoModeracion,
            com.uade.entrelibros.backend.entity.EstadoPublicacion estadoPublicacion, Pageable pageable);

    // Categorias activas de cada libro (idLibro -> nombres), en una sola consulta para toda la lista
    Map<Long, List<String>> getCategoriasPorLibro(List<Libro> libros);

    // Todas las categorias de cada libro con su estado: el admin ve al moderar si alguna esta dada de baja
    Map<Long, List<LibroResponse.CategoriaModeracion>> getCategoriasModeracionPorLibro(List<Libro> libros);
}