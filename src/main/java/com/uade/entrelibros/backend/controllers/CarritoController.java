package com.uade.entrelibros.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.CarritoItem;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.AgregarItemCarritoRequest;
import com.uade.entrelibros.backend.entity.dto.CheckoutRequest;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CarritoVacioException;
import com.uade.entrelibros.backend.exceptions.CompraPropiaException;
import com.uade.entrelibros.backend.exceptions.ItemCarritoNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.StockInsuficienteException;
import com.uade.entrelibros.backend.service.CarritoService;
import com.uade.entrelibros.backend.exceptions.LibroNoDisponibleException;

@RestController
@RequestMapping("carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping
    public ResponseEntity<List<CarritoItem>> getCarrito(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(carritoService.getItemsCarrito(usuario.getId()));
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoItem> agregarItem(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody AgregarItemCarritoRequest request)
            throws LibroNoEncontradoException, StockInsuficienteException, LibroNoDisponibleException,
            CompraPropiaException {
        CarritoItem result = carritoService.agregarItem(
                usuario.getId(), request.getIdLibro(), request.getCantidad());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/items/{idItem}")
    public ResponseEntity<Void> quitarItem(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idItem)
            throws ItemCarritoNoEncontradoException, AccionNoPermitidaException {
        carritoService.quitarItem(usuario.getId(), idItem);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<Orden> checkout(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody CheckoutRequest request)
            throws CarritoVacioException, StockInsuficienteException, LibroNoDisponibleException,
            CompraPropiaException {
        Orden orden = carritoService.checkout(usuario.getId(), request.getProvinciaDestino());
        return ResponseEntity.ok(orden);
    }
}
