package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.LibroRequest;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.service.LibroService;

@RestController
@RequestMapping("libros")
public class LibrosController {

    @Autowired
    private LibroService libroService;

    @GetMapping
    public ResponseEntity<List<Libro>> getLibros() {
        return ResponseEntity.ok(libroService.getLibros());
    }

    @GetMapping("/{libroId}")
    public ResponseEntity<Libro> getLibroById(@PathVariable Long libroId)
            throws LibroNoEncontradoException {
        return ResponseEntity.ok(libroService.getLibroById(libroId));
    }

    @PostMapping
    public ResponseEntity<Libro> createLibro(
            @AuthenticationPrincipal Usuario vendedor,
            @RequestBody LibroRequest request)
            throws CategoriaNoEncontradaException, RolInvalidoException {
        Libro result = libroService.createLibro(request, vendedor);
        return ResponseEntity.created(URI.create("/libros/" + result.getId())).body(result);
    }

    @PatchMapping("/{libroId}")
    public ResponseEntity<Libro> updateLibro(
            @AuthenticationPrincipal Usuario vendedor,
            @PathVariable Long libroId,
            @RequestBody LibroRequest request)
            throws LibroNoEncontradoException, CategoriaNoEncontradaException, RolInvalidoException,
            AccionNoPermitidaException {
        return ResponseEntity.ok(libroService.updateLibro(libroId, request, vendedor));
    }

    @DeleteMapping("/{libroId}")
    public ResponseEntity<Void> darDeBajaLibro(
            @AuthenticationPrincipal Usuario vendedor,
            @PathVariable Long libroId)
            throws LibroNoEncontradoException, RolInvalidoException, AccionNoPermitidaException {
        libroService.darDeBajaLibro(libroId, vendedor);
        return ResponseEntity.noContent().build();
    }
}
