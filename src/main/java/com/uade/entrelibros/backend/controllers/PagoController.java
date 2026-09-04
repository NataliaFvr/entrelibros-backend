package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Pago;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.PagoRequest;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoCancelableException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;
import com.uade.entrelibros.backend.service.PagoService;

@RestController
@RequestMapping("pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Pago>> getPagos() {
        return ResponseEntity.ok(pagoService.getPagos());
    }

    @GetMapping("/{idPago}")
    public ResponseEntity<Pago> getPagoById(
            @AuthenticationPrincipal Usuario comprador,
            @PathVariable Long idPago)
            throws PagoNoEncontradoException, AccionNoPermitidaException {
        return ResponseEntity.ok(pagoService.getPagoById(comprador, idPago));
    }

    @GetMapping("/orden/{idOrden}")
    public ResponseEntity<List<Pago>> getPagosByOrden(
            @AuthenticationPrincipal Usuario comprador,
            @PathVariable Long idOrden) throws OrdenNoEncontradaException, AccionNoPermitidaException {
        return ResponseEntity.ok(pagoService.getPagosByOrden(comprador, idOrden));
    }

    @PostMapping
    public ResponseEntity<Pago> crearPago(
            @AuthenticationPrincipal Usuario comprador,
            @RequestBody PagoRequest request)
            throws OrdenNoEncontradaException, AccionNoPermitidaException, OrdenNoCancelableException {
        Pago result = pagoService.crearPago(comprador, request.getIdOrden(), request.getProveedor());
        return ResponseEntity.created(URI.create("/pagos/" + result.getId())).body(result);
    }
}
