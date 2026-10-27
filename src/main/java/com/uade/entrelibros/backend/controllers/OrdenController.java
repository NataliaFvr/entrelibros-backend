package com.uade.entrelibros.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.OrdenVendedor;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.OrdenResponse;
import com.uade.entrelibros.backend.entity.dto.OrdenVendedorResponse;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoCancelableException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.OrdenVendedorNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.service.OrdenRespuestas;
import com.uade.entrelibros.backend.service.OrdenService;

@RestController
@RequestMapping("ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    // Arma las respuestas completas (libros, medio de pago, categorias y partes canceladas)
    @Autowired
    private OrdenRespuestas respuestas;

    @GetMapping
    public ResponseEntity<List<OrdenResponse>> getOrdenes(@AuthenticationPrincipal Usuario usuario)
            throws AccionNoPermitidaException {
        return ResponseEntity.ok(respuestas.lista(ordenService.getOrdenes(usuario)));
    }

    @GetMapping("/{idOrden}")
    public ResponseEntity<OrdenResponse> getOrdenById(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idOrden)
            throws OrdenNoEncontradaException, AccionNoPermitidaException {
        var orden = ordenService.getOrdenById(idOrden, usuario);
        List<OrdenItem> items = ordenService.getItemsDeOrden(idOrden);
        boolean esComprador = orden.getComprador() != null
                && orden.getComprador().getId().equals(usuario.getId());
        boolean esAdmin = usuario.getRol() == Rol.ADMIN;
        // El vendedor de la orden ve solo sus libros
        if (!esComprador && !esAdmin && usuario.getRol() == Rol.VENDEDOR) {
            items = items.stream()
                    .filter(item -> item.getVendedor() != null
                            && item.getVendedor().getId().equals(usuario.getId()))
                    .toList();
        }
        return ResponseEntity.ok(respuestas.conItems(orden, items));
    }

    @GetMapping("/comprador")
    public ResponseEntity<List<OrdenResponse>> getOrdenesByComprador(@AuthenticationPrincipal Usuario comprador) {
        return ResponseEntity.ok(respuestas.lista(ordenService.getOrdenesByComprador(comprador)));
    }

    @GetMapping("/vendedor")
    public ResponseEntity<List<OrdenVendedorResponse>> getOrdenesDelVendedor(@AuthenticationPrincipal Usuario vendedor)
            throws RolInvalidoException {
        List<OrdenVendedor> ventas = ordenService.getOrdenesDelVendedor(vendedor);
        return ResponseEntity.ok(respuestas.ventas(ventas, vendedor.getId()));
    }

    // Devuelve la venta completa (con sus libros): el front la reemplaza en su lista sin volver a pedirla
    @PatchMapping("/vendedor/{idOrdenVendedor}/cancelar")
    public ResponseEntity<OrdenVendedorResponse> cancelarOrdenVendedor(
            @AuthenticationPrincipal Usuario vendedor,
            @PathVariable Long idOrdenVendedor)
            throws OrdenVendedorNoEncontradaException, RolInvalidoException, AccionNoPermitidaException {
        return ResponseEntity.ok(respuestas.venta(ordenService.cancelarOrdenVendedor(idOrdenVendedor, vendedor)));
    }

    // Devuelve la orden completa (con sus libros): el front la reemplaza en su lista sin volver a pedirla
    @PatchMapping("/{idOrden}/cancelar")
    public ResponseEntity<OrdenResponse> cancelarOrden(
            @AuthenticationPrincipal Usuario comprador,
            @PathVariable Long idOrden)
            throws OrdenNoEncontradaException, AccionNoPermitidaException, OrdenNoCancelableException {
        return ResponseEntity.ok(respuestas.completa(ordenService.cancelarOrden(idOrden, comprador)));
    }
}
