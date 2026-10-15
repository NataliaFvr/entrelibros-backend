package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.ResenaVendedor;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.ResenaVendedorRequest;
import com.uade.entrelibros.backend.entity.dto.ResenaResumenResponse;
import com.uade.entrelibros.backend.entity.dto.ResenaVendedorEstadoResponse;
import com.uade.entrelibros.backend.entity.dto.ResenaVendedorResponse;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CalificacionInvalidaException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ResenaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.ResenaVendedorNoEncontradaException;
import com.uade.entrelibros.backend.service.ResenaVendedorService;

import jakarta.validation.Valid;
import com.uade.entrelibros.backend.repository.OrdenItemRepository;
import com.uade.entrelibros.backend.entity.OrdenItem;

@RestController
@RequestMapping("resenas-vendedor")
public class ResenaVendedorController {

    @Autowired
    private ResenaVendedorService resenaVendedorService;

    @Autowired
    private OrdenItemRepository ordenItemRepository;

    @GetMapping
    public ResponseEntity<List<ResenaVendedorResponse>> getResenas() {
        List<ResenaVendedorResponse> resultado = resenaVendedorService.getResenas().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{idResena}")
    public ResponseEntity<ResenaVendedorResponse> getResenaById(@PathVariable Long idResena)
            throws ResenaVendedorNoEncontradaException {
        ResenaVendedor resena = resenaVendedorService.getResenaById(idResena);
        return ResponseEntity.ok(toResponse(resena));
    }

    @GetMapping("/vendedor/{idVendedor}")
    public ResponseEntity<List<ResenaVendedorResponse>> getResenasByVendedor(@PathVariable Long idVendedor) {
        List<ResenaVendedorResponse> resultado = resenaVendedorService.getResenasByVendedor(idVendedor).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vendedor/{idVendedor}/estado")
    public ResponseEntity<ResenaVendedorEstadoResponse> getEstado(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idVendedor) {
        ResenaVendedorService.EstadoResena estado = resenaVendedorService.getEstadoResena(usuario, idVendedor);
        return ResponseEntity.ok(new ResenaVendedorEstadoResponse(
                estado.puedeResenar(), estado.motivo(), estado.idPago(),
                estado.miResena() != null ? toResponse(estado.miResena()) : null));
    }

    @GetMapping("/vendedor/{idVendedor}/resumen")
    public ResponseEntity<ResenaResumenResponse> getResumenByVendedor(@PathVariable Long idVendedor) {
        return ResponseEntity.ok(resenaVendedorService.getResumenByVendedor(idVendedor));
    }

    @PreAuthorize("hasAnyAuthority('COMPRADOR','VENDEDOR')")
    @PostMapping
    public ResponseEntity<ResenaVendedorResponse> crearResena(
            @AuthenticationPrincipal Usuario comprador,
            @Valid @RequestBody ResenaVendedorRequest request)
            throws PagoNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException,
            AccionNoPermitidaException {
        ResenaVendedor result = resenaVendedorService.crearResena(
                comprador, request.getIdPago(), request.getIdVendedor(),
                request.getClasificacion(), request.getComentario());
        return ResponseEntity.created(URI.create("/resenas-vendedor/" + result.getId()))
                .body(toResponse(result));
    }

    @PreAuthorize("hasAnyAuthority('COMPRADOR','VENDEDOR')")
    @PatchMapping("/{idResena}")
    public ResponseEntity<ResenaVendedorResponse> modificarResena(
            @PathVariable Long idResena,
            @AuthenticationPrincipal Usuario comprador,
            @Valid @RequestBody ResenaVendedorRequest request) {
        ResenaVendedor result = resenaVendedorService.modificarResena(
                idResena, comprador, request.getClasificacion(), request.getComentario());
        return ResponseEntity.ok(toResponse(result));
    }

    @DeleteMapping("/{idResena}")
    public ResponseEntity<Map<String, String>> eliminarResena(
            @PathVariable Long idResena,
            @AuthenticationPrincipal Usuario usuario) {
        resenaVendedorService.eliminarResena(idResena, usuario);
        return ResponseEntity.ok(Map.of("mensaje", "Reseña eliminada correctamente"));
    }

    private ResenaVendedorResponse toResponse(ResenaVendedor resena) {
        OrdenItem item = null;
        if (resena.getPago() != null && resena.getPago().getOrden() != null
                && resena.getVendedor() != null) {
            item = ordenItemRepository.findFirstByOrdenIdAndVendedorIdOrderByIdAsc(
                    resena.getPago().getOrden().getId(), resena.getVendedor().getId()).orElse(null);
        }
        return ResenaVendedorResponse.from(resena, item);
    }
}