package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.ResenaVendedor;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.ResenaVendedorRequest;
import com.uade.entrelibros.backend.entity.dto.ResenaVendedorResponse;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CalificacionInvalidaException;
import com.uade.entrelibros.backend.exceptions.EnvioItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ResenaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.ResenaVendedorNoEncontradaException;
import com.uade.entrelibros.backend.service.ResenaVendedorService;

@RestController
@RequestMapping("resenas-vendedor")
public class ResenaVendedorController {

    @Autowired
    private ResenaVendedorService resenaVendedorService;

    @GetMapping
    public ResponseEntity<List<ResenaVendedorResponse>> getResenas() {
        List<ResenaVendedorResponse> resultado = resenaVendedorService.getResenas().stream()
                .map(ResenaVendedorResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{idResena}")
    public ResponseEntity<ResenaVendedorResponse> getResenaById(@PathVariable Long idResena)
            throws ResenaVendedorNoEncontradaException {
        ResenaVendedor resena = resenaVendedorService.getResenaById(idResena);
        return ResponseEntity.ok(ResenaVendedorResponse.from(resena));
    }

    @GetMapping("/vendedor/{idVendedor}")
    public ResponseEntity<List<ResenaVendedorResponse>> getResenasByVendedor(@PathVariable Long idVendedor) {
        List<ResenaVendedorResponse> resultado = resenaVendedorService.getResenasByVendedor(idVendedor).stream()
                .map(ResenaVendedorResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<ResenaVendedorResponse> crearResena(
            @AuthenticationPrincipal Usuario comprador,
            @RequestBody ResenaVendedorRequest request)
            throws EnvioItemNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException,
            AccionNoPermitidaException {
        ResenaVendedor result = resenaVendedorService.crearResena(
                comprador, request.getIdEnvioItem(), request.getClasificacion(), request.getComentario());
        return ResponseEntity.created(URI.create("/resenas-vendedor/" + result.getId()))
                .body(ResenaVendedorResponse.from(result));
    }

    @PatchMapping("/{idResena}")
    public ResponseEntity<ResenaVendedorResponse> modificarResena(
            @PathVariable Long idResena,
            @AuthenticationPrincipal Usuario comprador,
            @RequestBody ResenaVendedorRequest request) {
        ResenaVendedor result = resenaVendedorService.modificarResena(
                idResena, comprador, request.getClasificacion(), request.getComentario());
        return ResponseEntity.ok(ResenaVendedorResponse.from(result));
    }

    @DeleteMapping("/{idResena}")
    public ResponseEntity<Void> eliminarResena(
            @PathVariable Long idResena,
            @AuthenticationPrincipal Usuario comprador) {
        resenaVendedorService.eliminarResena(idResena, comprador);
        return ResponseEntity.noContent().build();
    }
}
