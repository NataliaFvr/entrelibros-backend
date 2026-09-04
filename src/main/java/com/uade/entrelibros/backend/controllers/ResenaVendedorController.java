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
    public ResponseEntity<List<ResenaVendedor>> getResenas() {
        return ResponseEntity.ok(resenaVendedorService.getResenas());
    }

    @GetMapping("/{idResena}")
    public ResponseEntity<ResenaVendedor> getResenaById(@PathVariable Long idResena)
            throws ResenaVendedorNoEncontradaException {
        return ResponseEntity.ok(resenaVendedorService.getResenaById(idResena));
    }

    @GetMapping("/vendedor/{idVendedor}")
    public ResponseEntity<List<ResenaVendedor>> getResenasByVendedor(@PathVariable Long idVendedor) {
        return ResponseEntity.ok(resenaVendedorService.getResenasByVendedor(idVendedor));
    }

    @PostMapping
    public ResponseEntity<ResenaVendedor> crearResena(
            @AuthenticationPrincipal Usuario comprador,
            @RequestBody ResenaVendedorRequest request)
            throws EnvioItemNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException,
            AccionNoPermitidaException {
        ResenaVendedor result = resenaVendedorService.crearResena(
                comprador, request.getIdEnvioItem(), request.getClasificacion(), request.getComentario());
        return ResponseEntity.created(URI.create("/resenas-vendedor/" + result.getId())).body(result);
    }
}