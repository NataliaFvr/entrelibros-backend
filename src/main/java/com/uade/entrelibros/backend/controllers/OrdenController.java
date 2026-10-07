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
import com.uade.entrelibros.backend.repository.PagoRepository;
import com.uade.entrelibros.backend.repository.OrdenItemRepository;
import com.uade.entrelibros.backend.repository.LibroCategoriaRepository;
import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.Pago;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private OrdenItemRepository ordenItemRepository;

    @Autowired
    private LibroCategoriaRepository libroCategoriaRepository;

    @GetMapping
    public ResponseEntity<List<OrdenResponse>> getOrdenes(@AuthenticationPrincipal Usuario usuario)
            throws AccionNoPermitidaException {
        List<OrdenResponse> resultado = ordenService.getOrdenes(usuario).stream()
                .map(orden -> OrdenResponse.from(orden, List.of(), proveedorDe(orden.getId())))
                .toList();
        return ResponseEntity.ok(resultado);
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
        boolean esAdmin = usuario.getRol() == com.uade.entrelibros.backend.entity.Rol.ADMIN;
        if (!esComprador && !esAdmin
                && usuario.getRol() == com.uade.entrelibros.backend.entity.Rol.VENDEDOR) {
            items = items.stream()
                    .filter(item -> item.getVendedor() != null
                            && item.getVendedor().getId().equals(usuario.getId()))
                    .toList();
        }
        return ResponseEntity.ok(OrdenResponse.from(
                orden, items, proveedorDe(orden.getId()), categoriasPorLibro(items)));
    }

    @GetMapping("/comprador")
    public ResponseEntity<List<OrdenResponse>> getOrdenesByComprador(@AuthenticationPrincipal Usuario comprador) {
        List<OrdenResponse> resultado = ordenService.getOrdenesByComprador(comprador).stream()
                .map(orden -> OrdenResponse.from(orden, List.of(), proveedorDe(orden.getId())))
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/vendedor")
    public ResponseEntity<List<OrdenVendedorResponse>> getOrdenesDelVendedor(@AuthenticationPrincipal Usuario vendedor)
            throws RolInvalidoException {
        List<com.uade.entrelibros.backend.entity.OrdenVendedor> ventas =
                ordenService.getOrdenesDelVendedor(vendedor);
        List<Long> idsOrden = ventas.stream().map(venta -> venta.getOrden().getId()).toList();
        List<OrdenItem> itemsDelVendedor = ordenItemRepository
                .findByOrdenIdInAndVendedorId(idsOrden, vendedor.getId()).stream()
                .toList();
        Map<Long, List<OrdenItem>> itemsPorOrden = itemsDelVendedor.stream()
                .collect(Collectors.groupingBy(item -> item.getOrden().getId()));
        Map<Long, List<String>> categoriasPorLibro = categoriasPorLibro(itemsDelVendedor);
        Map<Long, String> proveedores = pagoRepository.findByOrdenIdIn(idsOrden).stream()
                .collect(Collectors.toMap(pago -> pago.getOrden().getId(), Pago::getProveedor, (a, b) -> a));
        List<OrdenVendedorResponse> resultado = ventas.stream()
                .map(venta -> OrdenVendedorResponse.from(
                        venta,
                        itemsPorOrden.getOrDefault(venta.getOrden().getId(), List.of()),
                        proveedores.get(venta.getOrden().getId()),
                        categoriasPorLibro))
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

    private String proveedorDe(Long idOrden) {
        return pagoRepository.findByOrdenId(idOrden).stream()
                .findFirst()
                .map(Pago::getProveedor)
                .orElse(null);
    }

    private Map<Long, List<String>> categoriasPorLibro(List<OrdenItem> items) {
        List<Long> idsLibro = items.stream()
                .filter(item -> item.getLibro() != null)
                .map(item -> item.getLibro().getId())
                .distinct()
                .toList();
        if (idsLibro.isEmpty()) {
            return Map.of();
        }
        return libroCategoriaRepository.findByLibroIdIn(idsLibro).stream()
                .collect(Collectors.groupingBy(
                        lc -> lc.getLibro().getId(),
                        Collectors.mapping(lc -> lc.getCategoria().getNombre(), Collectors.toList())));
    }
}
