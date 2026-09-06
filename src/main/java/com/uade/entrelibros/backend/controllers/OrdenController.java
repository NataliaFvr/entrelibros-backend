package com.uade.entrelibros.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.OrdenResponse;
import com.uade.entrelibros.backend.entity.dto.OrdenVendedorResponse;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.OrdenVendedorNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.exceptions.OrdenNoCancelableException;
import com.uade.entrelibros.backend.service.OrdenService;

@RestController
@RequestMapping("ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @GetMapping
    public ResponseEntity<List<OrdenResponse>> getOrdenes(@AuthenticationPrincipal Usuario usuario)
            throws AccionNoPermitidaException {
        List<OrdenResponse> resultado = ordenService.getOrdenes(usuario).stream()
                .map(OrdenResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{idOrden}")
    public ResponseEntity<OrdenResponse> getOrdenById(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idOrden)
            throws OrdenNoEncontradaException, AccionNoPermitidaException {
        return ResponseEntity.ok(OrdenResponse.from(ordenService.getOrdenById(idOrden, usuario)));
    }

    @GetMapping("/comprador")
    public ResponseEntity<List<OrdenResponse>> getOrdenesByComprador(@AuthenticationPrincipal Usuario comprador) {
        List<OrdenResponse> resultado = ordenService.getOrdenesByComprador(comprador).stream()
                .map(OrdenResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/vendedor")
    public ResponseEntity<List<OrdenVendedorResponse>> getOrdenesDelVendedor(@AuthenticationPrincipal Usuario vendedor)
            throws RolInvalidoException {
        List<OrdenVendedorResponse> resultado = ordenService.getOrdenesDelVendedor(vendedor).stream()
                .map(OrdenVendedorResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @PatchMapping("/vendedor/{idOrdenVendedor}/cancelar")
    public ResponseEntity<OrdenVendedorResponse> cancelarOrdenVendedor(
            @AuthenticationPrincipal Usuario vendedor,
            @PathVariable Long idOrdenVendedor)
            throws OrdenVendedorNoEncontradaException, RolInvalidoException, AccionNoPermitidaException {
        return ResponseEntity.ok(
                OrdenVendedorResponse.from(ordenService.cancelarOrdenVendedor(idOrdenVendedor, vendedor)));
    }

    @PatchMapping("/{idOrden}/cancelar")
    public ResponseEntity<OrdenResponse> cancelarOrden(
            @AuthenticationPrincipal Usuario comprador,
            @PathVariable Long idOrden)
            throws OrdenNoEncontradaException, AccionNoPermitidaException, OrdenNoCancelableException {
        return ResponseEntity.ok(OrdenResponse.from(ordenService.cancelarOrden(idOrden, comprador)));
    }
}
