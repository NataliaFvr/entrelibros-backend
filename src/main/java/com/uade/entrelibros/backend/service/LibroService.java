package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.VendedorNoEncontradoException;

public interface LibroService {
    List<Libro> getLibros();

    Libro getLibroById(Long libroId) throws LibroNoEncontradoException;

    Libro createLibro(String titulo, String autor, String editorial, Integer anio, String idioma,
            String estadoLibro, Double precio, Double descuentoPct, Integer stock, String descripcion,
            Long idVendedor, List<Long> idCategorias)
            throws VendedorNoEncontradoException, CategoriaNoEncontradaException;
}