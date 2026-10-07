package com.uade.entrelibros.backend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.CarritoItem;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.AgregarItemCarritoRequest;
import com.uade.entrelibros.backend.entity.dto.CarritoItemResponse;
import com.uade.entrelibros.backend.entity.dto.CheckoutRequest;
import com.uade.entrelibros.backend.entity.dto.ModificarCantidadCarritoRequest;
import com.uade.entrelibros.backend.entity.dto.OrdenResponse;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CantidadInvalidaException;
import com.uade.entrelibros.backend.exceptions.CarritoVacioException;
import com.uade.entrelibros.backend.exceptions.CompraPropiaException;
import com.uade.entrelibros.backend.exceptions.ItemCarritoNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.StockInsuficienteException;
import com.uade.entrelibros.backend.service.CarritoService;
import com.uade.entrelibros.backend.repository.ImagenLibroRepository;
import com.uade.entrelibros.backend.exceptions.LibroNoDisponibleException;

@RestController
@RequestMapping("carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ImagenLibroRepository imagenLibroRepository;

    @GetMapping
    public ResponseEntity<List<CarritoItemResponse>> getCarrito(@AuthenticationPrincipal Usuario usuario) {
        List<CarritoItemResponse> resultado = carritoService.getItemsCarrito(usuario.getId()).stream()
                .map(item -> CarritoItemResponse.from(item,
                        imagenLibroRepository.findFirstByLibroIdOrderByOrdenAsc(item.getLibro().getId())
                                .orElse(null)))
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoItemResponse> agregarItem(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody AgregarItemCarritoRequest request)
            throws LibroNoEncontradoException, StockInsuficienteException, LibroNoDisponibleException,
            CompraPropiaException {
        CarritoItem result = carritoService.agregarItem(
                usuario.getId(), request.getIdLibro(), request.getCantidad());
        return ResponseEntity.ok(CarritoItemResponse.from(result,
                imagenLibroRepository.findFirstByLibroIdOrderByOrdenAsc(result.getLibro().getId())
                        .orElse(null)));
    }

    @PatchMapping("/items/{idItem}")
    public ResponseEntity<CarritoItemResponse> modificarCantidad(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idItem,
            @RequestBody ModificarCantidadCarritoRequest request)
            throws ItemCarritoNoEncontradoException, AccionNoPermitidaException, CantidadInvalidaException,
            StockInsuficienteException, LibroNoDisponibleException {
        CarritoItem result = carritoService.modificarCantidad(
                usuario.getId(), idItem, request.getCantidad());
        return ResponseEntity.ok(CarritoItemResponse.from(result,
                imagenLibroRepository.findFirstByLibroIdOrderByOrdenAsc(result.getLibro().getId())
                        .orElse(null)));
    }

    @DeleteMapping("/items/{idItem}")
    public ResponseEntity<Map<String, String>> quitarItem(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idItem)
            throws ItemCarritoNoEncontradoException, AccionNoPermitidaException {
        carritoService.quitarItem(usuario.getId(), idItem);
        return ResponseEntity.ok(Map.of("mensaje", "Libro quitado del carrito"));
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrdenResponse> checkout(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody CheckoutRequest request)
            throws CarritoVacioException, StockInsuficienteException, LibroNoDisponibleException,
            CompraPropiaException, LibroNoEncontradoException {
        Orden orden = carritoService.checkout(usuario.getId(), request.getIdDireccion(), request.getProvinciaDestino());
        return ResponseEntity.ok(OrdenResponse.from(orden));
    }
}
