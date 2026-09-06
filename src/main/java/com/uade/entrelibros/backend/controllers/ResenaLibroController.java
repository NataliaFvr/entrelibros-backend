package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.ResenaLibro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.ResenaLibroRequest;
import com.uade.entrelibros.backend.entity.dto.ResenaLibroResponse;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CalificacionInvalidaException;
import com.uade.entrelibros.backend.exceptions.OrdenItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ResenaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.ResenaLibroNoEncontradaException;
import com.uade.entrelibros.backend.service.ResenaLibroService;

@RestController
@RequestMapping("resenas-libro")
public class ResenaLibroController {

    @Autowired
    private ResenaLibroService resenaLibroService;

    @GetMapping
    public ResponseEntity<List<ResenaLibroResponse>> getResenas() {
        List<ResenaLibroResponse> resultado = resenaLibroService.getResenas().stream()
                .map(ResenaLibroResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{idResena}")
    public ResponseEntity<ResenaLibroResponse> getResenaById(@PathVariable Long idResena)
            throws ResenaLibroNoEncontradaException {
        ResenaLibro resena = resenaLibroService.getResenaById(idResena);
        return ResponseEntity.ok(ResenaLibroResponse.from(resena));
    }

    @GetMapping("/libro/{idLibro}")
    public ResponseEntity<List<ResenaLibroResponse>> getResenasByLibro(@PathVariable Long idLibro) {
        List<ResenaLibroResponse> resultado = resenaLibroService.getResenasByLibro(idLibro).stream()
                .map(ResenaLibroResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<ResenaLibroResponse> crearResena(
            @AuthenticationPrincipal Usuario comprador,
            @RequestBody ResenaLibroRequest request)
            throws OrdenItemNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException,
            AccionNoPermitidaException {
        ResenaLibro result = resenaLibroService.crearResena(
                comprador, request.getIdOrdenItem(), request.getCalificacion(), request.getComentario());
        return ResponseEntity.created(URI.create("/resenas-libro/" + result.getId()))
                .body(ResenaLibroResponse.from(result));
    }

    @PatchMapping("/{idResena}")
    public ResponseEntity<ResenaLibroResponse> modificarResena(
            @PathVariable Long idResena,
            @AuthenticationPrincipal Usuario comprador,
            @RequestBody ResenaLibroRequest request) {
        ResenaLibro result = resenaLibroService.modificarResena(
                idResena, comprador, request.getCalificacion(), request.getComentario());
        return ResponseEntity.ok(ResenaLibroResponse.from(result));
    }

    @DeleteMapping("/{idResena}")
    public ResponseEntity<Void> eliminarResena(
            @PathVariable Long idResena,
            @AuthenticationPrincipal Usuario comprador) {
        resenaLibroService.eliminarResena(idResena, comprador);
        return ResponseEntity.noContent().build();
    }
}
