package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.LibroFiltroRequest;
import com.uade.entrelibros.backend.entity.dto.LibroRequest;
import com.uade.entrelibros.backend.entity.dto.ModeracionRequest;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.service.LibroService;
import org.springframework.data.domain.Page;


@RestController
@RequestMapping("libros")
public class LibrosController {

    @Autowired
    private LibroService libroService;

    @GetMapping
    public ResponseEntity<Page<Libro>> getLibros(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) List<Long> idCategorias,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) List<String> editoriales,
            @RequestParam(required = false) List<String> autores,
            @RequestParam(required = false) List<String> idiomas,
            @RequestParam(required = false) List<Integer> anios,
            @RequestParam(required = false) Boolean soloConDescuento,
            @RequestParam(required = false) List<Long> idVendedores,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        LibroFiltroRequest filtro = new LibroFiltroRequest();
        filtro.setTexto(texto);
        filtro.setIdCategorias(idCategorias);
        filtro.setPrecioMin(precioMin);
        filtro.setPrecioMax(precioMax);
        filtro.setEditoriales(editoriales);
        filtro.setAutores(autores);
        filtro.setIdiomas(idiomas);
        filtro.setAnios(anios);
        filtro.setSoloConDescuento(soloConDescuento);
        filtro.setIdVendedores(idVendedores);

        return ResponseEntity.ok(libroService.buscarLibros(filtro, PageRequest.of(page, size)));
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{libroId}/moderacion")
    public ResponseEntity<Libro> moderarLibro(
            @PathVariable Long libroId,
            @RequestBody ModeracionRequest request)
        throws LibroNoEncontradoException {
        Libro result = libroService.moderarLibro(libroId, request.getEstadoModeracion());
        return ResponseEntity.ok(result);
    }
}
