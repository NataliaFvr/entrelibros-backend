package com.uade.entrelibros.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.CarritoItem;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.dto.AgregarItemCarritoRequest;
import com.uade.entrelibros.backend.entity.dto.CheckoutRequest;
import com.uade.entrelibros.backend.exceptions.CarritoVacioException;
import com.uade.entrelibros.backend.exceptions.ItemCarritoNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.StockInsuficienteException;
import com.uade.entrelibros.backend.service.CarritoService;

@RestController
@RequestMapping("carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/{idUsuario}")
    public ResponseEntity<List<CarritoItem>> getCarrito(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(carritoService.getItemsCarrito(idUsuario));
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoItem> agregarItem(@RequestBody AgregarItemCarritoRequest request)
            throws LibroNoEncontradoException, StockInsuficienteException {
        CarritoItem result = carritoService.agregarItem(
                request.getIdUsuario(), request.getIdLibro(), request.getCantidad());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/items/{idItem}")
    public ResponseEntity<Void> quitarItem(@PathVariable Long idItem)
            throws ItemCarritoNoEncontradoException {
        carritoService.quitarItem(idItem);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<Orden> checkout(@RequestBody CheckoutRequest request)
            throws CarritoVacioException, StockInsuficienteException {
        Orden orden = carritoService.checkout(request.getIdUsuario(), request.getProvinciaDestino());
        return ResponseEntity.ok(orden);
    }
}