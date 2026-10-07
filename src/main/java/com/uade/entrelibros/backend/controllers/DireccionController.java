package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Direccion;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.DireccionRequest;
import com.uade.entrelibros.backend.entity.dto.DireccionResponse;
import com.uade.entrelibros.backend.service.DireccionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("direcciones")
public class DireccionController {

    @Autowired
    private DireccionService direccionService;

    @GetMapping
    public ResponseEntity<List<DireccionResponse>> getDirecciones(@AuthenticationPrincipal Usuario usuario) {
        List<DireccionResponse> resultado = direccionService.getDirecciones(usuario.getId()).stream()
                .map(DireccionResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<DireccionResponse> crear(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody DireccionRequest request) {
        Direccion result = direccionService.crear(usuario, request);
        return ResponseEntity.created(URI.create("/direcciones/" + result.getId()))
                .body(DireccionResponse.from(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id) {
        direccionService.eliminar(usuario.getId(), id);
        return ResponseEntity.ok(Map.of("mensaje", "Direccion eliminada"));
    }

    @PatchMapping("/{id}/principal")
    public ResponseEntity<List<DireccionResponse>> marcarPrincipal(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id) {
        List<DireccionResponse> resultado = direccionService.marcarPrincipal(usuario.getId(), id).stream()
                .map(DireccionResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }
}