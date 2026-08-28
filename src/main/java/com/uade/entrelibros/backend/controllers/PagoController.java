package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Pago;
import com.uade.entrelibros.backend.entity.dto.PagoRequest;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;
import com.uade.entrelibros.backend.service.PagoService;

@RestController
@RequestMapping("pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping
    public ResponseEntity<List<Pago>> getPagos() {
        return ResponseEntity.ok(pagoService.getPagos());
    }

    @GetMapping("/{idPago}")
    public ResponseEntity<Pago> getPagoById(@PathVariable Long idPago)
            throws PagoNoEncontradoException {
        return ResponseEntity.ok(pagoService.getPagoById(idPago));
    }

    @GetMapping("/orden/{idOrden}")
    public ResponseEntity<List<Pago>> getPagosByOrden(@PathVariable Long idOrden) {
        return ResponseEntity.ok(pagoService.getPagosByOrden(idOrden));
    }

    @PostMapping
    public ResponseEntity<Pago> crearPago(@RequestBody PagoRequest request)
            throws OrdenNoEncontradaException {
        Pago result = pagoService.crearPago(request.getIdOrden(), request.getProveedor());
        return ResponseEntity.created(URI.create("/pagos/" + result.getId())).body(result);
    }
}
