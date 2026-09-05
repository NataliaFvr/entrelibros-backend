package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.Categoria;
import com.uade.entrelibros.backend.exceptions.CategoriaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;

public interface CategoriaService {
    List<Categoria> getCategorias();

    Categoria getCategoriaById(Long categoriaId);

    Categoria createCategoria(String nombre);
}
