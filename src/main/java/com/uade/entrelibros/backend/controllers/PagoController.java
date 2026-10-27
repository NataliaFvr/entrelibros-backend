package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Pago;
import com.uade.entrelibros.backend.entity.dto.PagoResponse;
import com.uade.entrelibros.backend.entity.dto.PagosResumenResponse;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.PagoRequest;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoPagableException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;
import com.uade.entrelibros.backend.service.OrdenRespuestas;
import com.uade.entrelibros.backend.service.PagoService;

@RestController
@RequestMapping("pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private OrdenRespuestas respuestas;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PagoResponse>> getPagos() {
        List<PagoResponse> resultado = pagoService.getPagos().stream()
                .map(PagoResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    // Totales para el panel del admin (cobrado, aprobados, rechazados). Va antes de /{idPago}.
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/resumen")
    public ResponseEntity<PagosResumenResponse> getResumen() {
        return ResponseEntity.ok(pagoService.getResumen());
    }

    @GetMapping("/{idPago}")
    public ResponseEntity<PagoResponse> getPagoById(
            @AuthenticationPrincipal Usuario comprador,
            @PathVariable Long idPago)
            throws PagoNoEncontradoException, AccionNoPermitidaException {
        Pago pago = pagoService.getPagoById(comprador, idPago);
        return ResponseEntity.ok(PagoResponse.from(pago));
    }

    @GetMapping("/orden/{idOrden}")
    public ResponseEntity<List<PagoResponse>> getPagosByOrden(
            @AuthenticationPrincipal Usuario comprador,
            @PathVariable Long idOrden) throws OrdenNoEncontradaException, AccionNoPermitidaException {
        List<PagoResponse> resultado = pagoService.getPagosByOrden(comprador, idOrden).stream()
                .map(PagoResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    // Pagan COMPRADOR y VENDEDOR (un vendedor puede comprar libros que no sean suyos). El ADMIN no compra.
    @PreAuthorize("hasAnyAuthority('COMPRADOR','VENDEDOR')")
    @PostMapping
    public ResponseEntity<PagoResponse> crearPago(
            @AuthenticationPrincipal Usuario comprador,
            @RequestBody PagoRequest request)
            throws OrdenNoEncontradaException, AccionNoPermitidaException, OrdenNoPagableException {
        Pago result = pagoService.crearPago(comprador, request.getIdOrden(), request.getProveedor());
        // Junto con el pago va la orden completa ya pagada: el front la reemplaza en su lista sin volver a pedirla
        PagoResponse respuesta = PagoResponse.from(result);
        respuesta.setOrden(respuestas.completa(result.getOrden()));
        return ResponseEntity.created(URI.create("/pagos/" + result.getId())).body(respuesta);
    }
}