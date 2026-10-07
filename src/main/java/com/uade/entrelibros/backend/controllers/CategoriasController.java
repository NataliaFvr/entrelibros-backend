package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Categoria;
import com.uade.entrelibros.backend.entity.dto.CategoriaRequest;
import com.uade.entrelibros.backend.entity.dto.CategoriaResponse;
import com.uade.entrelibros.backend.exceptions.CategoriaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.service.CategoriaService;
import com.uade.entrelibros.backend.service.ImagenCategoriaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("categorias")
public class CategoriasController {

    @Autowired
    private CategoriaService categoriaService; // inyeccion de dependencias :)

    @Autowired
    private ImagenCategoriaService imagenCategoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> getCategorias() {
        return ResponseEntity.ok(categoriaService.getCategorias().stream()
                .map(categoria -> CategoriaResponse.from(
                        categoria, imagenCategoriaService.tieneImagen(categoria.getId())))
                .toList());
    }

    @GetMapping("/{categoriaId}")
    public ResponseEntity<CategoriaResponse> getCategoriaById(@PathVariable Long categoriaId)
            throws CategoriaNoEncontradaException {
        Categoria categoria = categoriaService.getCategoriaById(categoriaId);
        return ResponseEntity.ok(CategoriaResponse.from(
                categoria, imagenCategoriaService.tieneImagen(categoriaId)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoriaResponse> createCategoria(@Valid @RequestBody CategoriaRequest request)
            throws CategoriaDuplicadaException {
        Categoria result = categoriaService.createCategoria(request.getNombre());
        return ResponseEntity.created(URI.create("/categorias/" + result.getId()))
                .body(CategoriaResponse.from(result, false));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{categoriaId}")
    public ResponseEntity<CategoriaResponse> renameCategoria(
            @PathVariable Long categoriaId,
            @Valid @RequestBody CategoriaRequest request) {
        Categoria result = categoriaService.renombrarCategoria(categoriaId, request.getNombre());
        return ResponseEntity.ok(CategoriaResponse.from(
                result, imagenCategoriaService.tieneImagen(categoriaId)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{categoriaId}/baja")
    public ResponseEntity<CategoriaResponse> deactivateCategoria(@PathVariable Long categoriaId) {
        Categoria result = categoriaService.cambiarEstado(categoriaId, false);
        return ResponseEntity.ok(CategoriaResponse.from(
                result, imagenCategoriaService.tieneImagen(categoriaId)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{categoriaId}/reactivar")
    public ResponseEntity<CategoriaResponse> activateCategoria(@PathVariable Long categoriaId) {
        Categoria result = categoriaService.cambiarEstado(categoriaId, true);
        return ResponseEntity.ok(CategoriaResponse.from(
                result, imagenCategoriaService.tieneImagen(categoriaId)));
    }
}