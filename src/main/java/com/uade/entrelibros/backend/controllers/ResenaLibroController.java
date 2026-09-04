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
    public ResponseEntity<List<ResenaLibro>> getResenas() {
        return ResponseEntity.ok(resenaLibroService.getResenas());
    }

    @GetMapping("/{idResena}")
    public ResponseEntity<ResenaLibro> getResenaById(@PathVariable Long idResena)
            throws ResenaLibroNoEncontradaException {
        return ResponseEntity.ok(resenaLibroService.getResenaById(idResena));
    }

    @GetMapping("/libro/{idLibro}")
    public ResponseEntity<List<ResenaLibro>> getResenasByLibro(@PathVariable Long idLibro) {
        return ResponseEntity.ok(resenaLibroService.getResenasByLibro(idLibro));
    }

    @PostMapping
    public ResponseEntity<ResenaLibro> crearResena(
            @AuthenticationPrincipal Usuario comprador,
            @RequestBody ResenaLibroRequest request)
            throws OrdenItemNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException,
            AccionNoPermitidaException {
        ResenaLibro result = resenaLibroService.crearResena(
                comprador, request.getIdOrdenItem(), request.getCalificacion(), request.getComentario());
        return ResponseEntity.created(URI.create("/resenas-libro/" + result.getId())).body(result);
    }
}