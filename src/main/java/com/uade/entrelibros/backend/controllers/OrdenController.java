package com.uade.entrelibros.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.OrdenVendedor;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.OrdenVendedorNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.service.OrdenService;

@RestController
@RequestMapping("ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @GetMapping
    public ResponseEntity<List<Orden>> getOrdenes(@AuthenticationPrincipal Usuario usuario)
            throws AccionNoPermitidaException {
        return ResponseEntity.ok(ordenService.getOrdenes(usuario));
    }

    @GetMapping("/{idOrden}")
    public ResponseEntity<Orden> getOrdenById(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idOrden)
            throws OrdenNoEncontradaException, AccionNoPermitidaException {
        return ResponseEntity.ok(ordenService.getOrdenById(idOrden, usuario));
    }

    @GetMapping("/comprador")
    public ResponseEntity<List<Orden>> getOrdenesByComprador(@AuthenticationPrincipal Usuario comprador) {
        return ResponseEntity.ok(ordenService.getOrdenesByComprador(comprador));
    }

    @GetMapping("/vendedor")
    public ResponseEntity<List<OrdenVendedor>> getOrdenesDelVendedor(@AuthenticationPrincipal Usuario vendedor)
            throws RolInvalidoException {
        return ResponseEntity.ok(ordenService.getOrdenesDelVendedor(vendedor));
    }

    @PatchMapping("/vendedor/{idOrdenVendedor}/cancelar")
    public ResponseEntity<OrdenVendedor> cancelarOrdenVendedor(
            @AuthenticationPrincipal Usuario vendedor,
            @PathVariable Long idOrdenVendedor)
            throws OrdenVendedorNoEncontradaException, RolInvalidoException, AccionNoPermitidaException {
        return ResponseEntity.ok(ordenService.cancelarOrdenVendedor(idOrdenVendedor, vendedor));
    }
}
