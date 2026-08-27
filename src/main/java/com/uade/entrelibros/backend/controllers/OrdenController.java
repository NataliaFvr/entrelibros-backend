package com.uade.entrelibros.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.service.OrdenService;

@RestController
@RequestMapping("ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @GetMapping
    public ResponseEntity<List<Orden>> getOrdenes() {
        return ResponseEntity.ok(ordenService.getOrdenes());
    }

    @GetMapping("/{idOrden}")
    public ResponseEntity<Orden> getOrdenById(@PathVariable Long idOrden)
            throws OrdenNoEncontradaException {
        return ResponseEntity.ok(ordenService.getOrdenById(idOrden));
    }

    @GetMapping("/comprador/{idComprador}")
    public ResponseEntity<List<Orden>> getOrdenesByComprador(@PathVariable Long idComprador) {
        return ResponseEntity.ok(ordenService.getOrdenesByComprador(idComprador));
    }
}