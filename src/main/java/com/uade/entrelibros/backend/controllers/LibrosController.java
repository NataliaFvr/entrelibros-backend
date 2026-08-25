package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.dto.LibroRequest;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.VendedorNoEncontradoException;
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
    public ResponseEntity<Libro> createLibro(@RequestBody LibroRequest request)
            throws VendedorNoEncontradoException, CategoriaNoEncontradaException {
        Libro result = libroService.createLibro(
                request.getTitulo(), request.getAutor(), request.getEditorial(), request.getAnio(),
                request.getIdioma(), request.getEstadoLibro(), request.getPrecio(),
                request.getDescuentoPct(), request.getStock(), request.getDescripcion(),
                request.getIdVendedor(), request.getIdCategorias());
        return ResponseEntity.created(URI.create("/libros/" + result.getId())).body(result);
    }
}