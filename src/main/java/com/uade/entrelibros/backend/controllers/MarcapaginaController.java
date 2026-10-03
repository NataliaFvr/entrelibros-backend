package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Marcapagina;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.MarcapaginaRequest;
import com.uade.entrelibros.backend.entity.dto.MarcapaginaResponse;
import com.uade.entrelibros.backend.service.MarcapaginaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("marcapaginas")
public class MarcapaginaController {

    @Autowired
    private MarcapaginaService marcapaginaService;

    @GetMapping
    public ResponseEntity<Page<MarcapaginaResponse>> getMarcapaginas(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<MarcapaginaResponse> resultado = marcapaginaService
                .getMarcapaginas(usuario.getId(), PageRequest.of(page, size))
                .map(MarcapaginaResponse::from);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<MarcapaginaResponse> agregar(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody MarcapaginaRequest request) {
        Marcapagina result = marcapaginaService.agregar(usuario, request.getIdLibro());
        return ResponseEntity.created(URI.create("/marcapaginas/" + request.getIdLibro()))
                .body(MarcapaginaResponse.from(result));
    }

    @DeleteMapping("/{idLibro}")
    public ResponseEntity<Map<String, String>> quitar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idLibro) {
        marcapaginaService.quitar(usuario.getId(), idLibro);
        return ResponseEntity.ok(Map.of("mensaje", "Libro quitado de tus marcapaginas"));
    }
}