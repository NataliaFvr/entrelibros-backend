package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.ResenaLibro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.ResenaLibroRequest;
import com.uade.entrelibros.backend.entity.dto.ResenaLibroEstadoResponse;
import com.uade.entrelibros.backend.entity.dto.ResenaLibroResponse;
import com.uade.entrelibros.backend.entity.dto.ResenaResumenResponse;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CalificacionInvalidaException;
import com.uade.entrelibros.backend.exceptions.OrdenItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ResenaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.ResenaLibroNoEncontradaException;
import com.uade.entrelibros.backend.service.ResenaLibroService;

import jakarta.validation.Valid;

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
    public ResponseEntity<List<ResenaLibroResponse>> getResenasByLibro(
            @PathVariable Long idLibro,
            @RequestParam(required = false) Integer calificacion,
            @RequestParam(required = false) String sort) {
        List<ResenaLibroResponse> resultado = resenaLibroService
                .getResenasByLibro(idLibro, calificacion, sort).stream()
                .map(ResenaLibroResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/vendedor/{idVendedor}/opiniones-libros")
    public ResponseEntity<List<ResenaLibroResponse>> getOpinionesByVendedor(
            @PathVariable Long idVendedor) {
        List<ResenaLibroResponse> resultado = resenaLibroService
                .getResenasByVendedor(idVendedor).stream()
                .map(ResenaLibroResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }
    
    // ¿Puede la persona logueada reseñar este libro? ¿Cual es su reseña? Lo decide el back (el front solo muestra el formulario)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/libro/{idLibro}/estado")
    public ResponseEntity<ResenaLibroEstadoResponse> getEstado(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idLibro) {
        ResenaLibroService.EstadoResena estado = resenaLibroService.getEstadoResena(usuario, idLibro);
        return ResponseEntity.ok(new ResenaLibroEstadoResponse(
                estado.puedeResenar(), estado.motivo(), estado.idOrdenItem(),
                estado.miResena() != null ? ResenaLibroResponse.from(estado.miResena()) : null));
    }

    // Promedio, cantidad y reparto por estrellas, calculados en el back
    @GetMapping("/libro/{idLibro}/resumen")
    public ResponseEntity<ResenaResumenResponse> getResumenByLibro(@PathVariable Long idLibro) {
        return ResponseEntity.ok(resenaLibroService.getResumenByLibro(idLibro));
    }

    @GetMapping("/vendedor/{idVendedor}/opiniones-libros/resumen")
    public ResponseEntity<ResenaResumenResponse> getResumenOpinionesByVendedor(@PathVariable Long idVendedor) {
        return ResponseEntity.ok(resenaLibroService.getResumenByVendedor(idVendedor));
    }

    @PreAuthorize("hasAnyAuthority('COMPRADOR','VENDEDOR')")
    @PostMapping
    public ResponseEntity<ResenaLibroResponse> crearResena(
            @AuthenticationPrincipal Usuario comprador,
            @Valid @RequestBody ResenaLibroRequest request)
            throws OrdenItemNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException,
            AccionNoPermitidaException {
        ResenaLibro result = resenaLibroService.crearResena(
                comprador, request.getIdOrdenItem(), request.getCalificacion(), request.getComentario());
        return ResponseEntity.created(URI.create("/resenas-libro/" + result.getId()))
                .body(ResenaLibroResponse.from(result));
    }

    @PreAuthorize("hasAnyAuthority('COMPRADOR','VENDEDOR')")
    @PatchMapping("/{idResena}")
    public ResponseEntity<ResenaLibroResponse> modificarResena(
            @PathVariable Long idResena,
            @AuthenticationPrincipal Usuario comprador,
            @Valid @RequestBody ResenaLibroRequest request) {
        ResenaLibro result = resenaLibroService.modificarResena(
                idResena, comprador, request.getCalificacion(), request.getComentario());
        return ResponseEntity.ok(ResenaLibroResponse.from(result));
    }

    @DeleteMapping("/{idResena}")
    public ResponseEntity<Map<String, String>> eliminarResena(
            @PathVariable Long idResena,
            @AuthenticationPrincipal Usuario usuario) {
        resenaLibroService.eliminarResena(idResena, usuario);
        return ResponseEntity.ok(Map.of("mensaje", "Reseña eliminada correctamente"));
    }
}