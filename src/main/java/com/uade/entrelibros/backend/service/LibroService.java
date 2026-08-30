package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.entity.dto.LibroRequest;

public interface LibroService {
    List<Libro> getLibros();

    Libro getLibroById(Long libroId) throws LibroNoEncontradoException;

    Libro createLibro(LibroRequest request, Usuario vendedor)
            throws CategoriaNoEncontradaException, RolInvalidoException;

    Libro updateLibro(Long libroId, LibroRequest request, Usuario vendedor)
            throws LibroNoEncontradoException, CategoriaNoEncontradaException, RolInvalidoException,
            AccionNoPermitidaException;

    void darDeBajaLibro(Long libroId, Usuario vendedor)
            throws LibroNoEncontradoException, RolInvalidoException, AccionNoPermitidaException;
}
