package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Categoria;
import com.uade.entrelibros.backend.exceptions.CategoriaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.repository.CategoriaRepository;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> getCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria getCategoriaById(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(CategoriaNoEncontradaException::new);
    }

    public Categoria createCategoria(String nombre) {
        Categoria existente = categoriaRepository.findByNombre(nombre);
        if (existente != null)
            throw new CategoriaDuplicadaException();
        return categoriaRepository.save(new Categoria(nombre));
    }
}
