package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Categoria;
import com.uade.entrelibros.backend.entity.dto.CategoriaRequest;
import com.uade.entrelibros.backend.exceptions.CategoriaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.service.CategoriaService;

@RestController
@RequestMapping("categorias")
public class CategoriasController {

    @Autowired
    private CategoriaService categoriaService; // inyeccion de dependencias :)

    @GetMapping
    public ResponseEntity<List<Categoria>> getCategorias() {
        return ResponseEntity.ok(categoriaService.getCategorias());
    }

    @GetMapping("/{categoriaId}")
    public ResponseEntity<Categoria> getCategoriaById(@PathVariable Long categoriaId)
            throws CategoriaNoEncontradaException {
        return ResponseEntity.ok(categoriaService.getCategoriaById(categoriaId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Categoria> createCategoria(@RequestBody CategoriaRequest request)
            throws CategoriaDuplicadaException {
        Categoria result = categoriaService.createCategoria(request.getNombre());
        return ResponseEntity.created(URI.create("/categorias/" + result.getId())).body(result);
    }
}