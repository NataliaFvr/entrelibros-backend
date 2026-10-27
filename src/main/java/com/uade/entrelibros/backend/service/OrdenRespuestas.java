package com.uade.entrelibros.backend.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uade.entrelibros.backend.entity.EstadoOrdenVendedor;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.OrdenVendedor;
import com.uade.entrelibros.backend.entity.Pago;
import com.uade.entrelibros.backend.entity.dto.OrdenResponse;
import com.uade.entrelibros.backend.entity.dto.OrdenVendedorResponse;
import com.uade.entrelibros.backend.repository.LibroCategoriaRepository;
import com.uade.entrelibros.backend.repository.OrdenItemRepository;
import com.uade.entrelibros.backend.repository.OrdenVendedorRepository;
import com.uade.entrelibros.backend.repository.PagoRepository;

// Arma las respuestas COMPLETAS de las ordenes (libros, medio de pago, categorias y partes canceladas).
// La usan las listas y tambien el checkout, el pago y las cancelaciones: asi, despues de guardar algo,
// el front actualiza su estado con lo que devolvio el back, sin volver a pedir la lista con un GET.
@Component
public class OrdenRespuestas {

    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private OrdenItemRepository ordenItemRepository;
    @Autowired
    private LibroCategoriaRepository libroCategoriaRepository;
    @Autowired
    private OrdenVendedorRepository ordenVendedorRepository;

    // Una orden con todos sus libros
    public OrdenResponse completa(Orden orden) {
        return conItems(orden, ordenItemRepository.findByOrdenId(orden.getId()));
    }

    // Una orden con los libros que se le pasan (el vendedor ve solo los suyos)
    public OrdenResponse conItems(Orden orden, List<OrdenItem> items) {
        return OrdenResponse.from(orden, items, proveedorDe(orden.getId()), categoriasPorLibro(items),
                vendedoresCancelados(List.of(orden.getId())).getOrDefault(orden.getId(), Set.of()));
    }

    // Las listas incluyen los libros en lote, para que el front no tenga que pedir el detalle una por una
    public List<OrdenResponse> lista(List<Orden> ordenes) {
        List<Long> idsOrden = ordenes.stream().map(Orden::getId).toList();
        if (idsOrden.isEmpty()) {
            return List.of();
        }
        List<OrdenItem> items = ordenItemRepository.findByOrdenIdIn(idsOrden);
        Map<Long, List<OrdenItem>> itemsPorOrden = items.stream()
                .collect(Collectors.groupingBy(item -> item.getOrden().getId()));
        Map<Long, List<String>> categorias = categoriasPorLibro(items);
        Map<Long, String> proveedores = proveedores(idsOrden);
        Map<Long, Set<Long>> cancelados = vendedoresCancelados(idsOrden);
        return ordenes.stream().map(orden -> OrdenResponse.from(orden,
                itemsPorOrden.getOrDefault(orden.getId(), List.of()), proveedores.get(orden.getId()), categorias,
                cancelados.getOrDefault(orden.getId(), Set.of()))).toList();
    }

    // Una venta (la parte de un vendedor) con sus libros
    public OrdenVendedorResponse venta(OrdenVendedor venta) {
        return ventas(List.of(venta), venta.getVendedor().getId()).get(0);
    }

    public List<OrdenVendedorResponse> ventas(List<OrdenVendedor> ventas, Long idVendedor) {
        List<Long> idsOrden = ventas.stream().map(venta -> venta.getOrden().getId()).toList();
        if (idsOrden.isEmpty()) {
            return List.of();
        }
        List<OrdenItem> itemsDelVendedor = ordenItemRepository.findByOrdenIdInAndVendedorId(idsOrden, idVendedor);
        Map<Long, List<OrdenItem>> itemsPorOrden = itemsDelVendedor.stream()
                .collect(Collectors.groupingBy(item -> item.getOrden().getId()));
        Map<Long, List<String>> categorias = categoriasPorLibro(itemsDelVendedor);
        Map<Long, String> proveedores = proveedores(idsOrden);
        return ventas.stream()
                .map(venta -> OrdenVendedorResponse.from(
                        venta,
                        itemsPorOrden.getOrDefault(venta.getOrden().getId(), List.of()),
                        proveedores.get(venta.getOrden().getId()),
                        categorias))
                .toList();
    }

    private String proveedorDe(Long idOrden) {
        return pagoRepository.findByOrdenId(idOrden).stream()
                .findFirst()
                .map(Pago::getProveedor)
                .orElse(null);
    }

    private Map<Long, String> proveedores(List<Long> idsOrden) {
        return pagoRepository.findByOrdenIdIn(idsOrden).stream()
                .collect(Collectors.toMap(pago -> pago.getOrden().getId(), Pago::getProveedor, (a, b) -> a));
    }

    // Por orden, los vendedores que cancelaron su parte
    private Map<Long, Set<Long>> vendedoresCancelados(List<Long> idsOrden) {
        return ordenVendedorRepository.findByOrdenIdIn(idsOrden).stream()
                .filter(ov -> ov.getEstado() == EstadoOrdenVendedor.CANCELADA)
                .collect(Collectors.groupingBy(ov -> ov.getOrden().getId(),
                        Collectors.mapping(ov -> ov.getVendedor().getId(), Collectors.toSet())));
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
