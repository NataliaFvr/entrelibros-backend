package com.uade.entrelibros.backend.service;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.EstadoPago;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.OrdenVendedor;
import com.uade.entrelibros.backend.entity.EstadoOrdenVendedor;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.OrdenVendedorNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoCancelableException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.exceptions.VentaPagadaNoCancelableException;
import com.uade.entrelibros.backend.repository.OrdenRepository;
import com.uade.entrelibros.backend.repository.OrdenVendedorRepository;
import com.uade.entrelibros.backend.repository.OrdenItemRepository;
import com.uade.entrelibros.backend.repository.LibroRepository;

@Service
public class OrdenServiceImpl implements OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;
    @Autowired
    private OrdenVendedorRepository ordenVendedorRepository;
    @Autowired
    private OrdenItemRepository ordenItemRepository;
    @Autowired
    private LibroRepository libroRepository;

    // Las lecturas de ordenes son transaccionales (de escritura) porque resuelven los vencimientos al consultar:
    // una reserva pasada se libera (y devuelve el stock) en el momento, sin esperar al temporizador de 60 s.
    // Asi el estado que se devuelve es el definitivo y el front no tiene que deducirlo.
    @Transactional
    public List<Orden> getOrdenes(Usuario usuario) {
        if (usuario.getRol() != Rol.ADMIN) {
            throw new AccionNoPermitidaException();
        }
        List<Orden> ordenes = ordenRepository.findAll();
        if (ordenes.isEmpty()) {
            throw new ListaVaciaException("No hay órdenes registradas");
        }
        ordenes.forEach(this::resolverVencimiento);
        return ordenes;
    }
    @Transactional
    public Orden getOrdenById(Long idOrden, Usuario usuario) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(OrdenNoEncontradaException::new);

        boolean esComprador = orden.getComprador().getId().equals(usuario.getId());
        boolean esVendedorDeLaOrden = ordenVendedorRepository.findByOrdenIdAndVendedorId(idOrden, usuario.getId())
                .isPresent();

        if (usuario.getRol() != Rol.ADMIN && !esComprador && !esVendedorDeLaOrden) {
            throw new AccionNoPermitidaException();
        }

        resolverVencimiento(orden);
        return orden;
    }

    public List<OrdenItem> getItemsDeOrden(Long idOrden) {
        return ordenItemRepository.findByOrdenId(idOrden);
    }

    @Transactional
    public List<Orden> getOrdenesByComprador(Usuario comprador) {
        List<Orden> ordenes = ordenRepository.findByCompradorId(comprador.getId());
        if (ordenes.isEmpty()) {
            throw new ListaVaciaException("No tenés órdenes registradas");
        }
        ordenes.forEach(this::resolverVencimiento);
        return ordenes;
    }

    @Transactional
    public List<OrdenVendedor> getOrdenesDelVendedor(Usuario vendedor) {
        validarVendedor(vendedor);
        List<OrdenVendedor> ordenes = ordenVendedorRepository.findByVendedorId(vendedor.getId());
        if (ordenes.isEmpty()) {
            throw new ListaVaciaException("No tenés órdenes como vendedor");
        }
        ordenes.forEach(ordenVendedor -> resolverVencimiento(ordenVendedor.getOrden()));
        return ordenes;
    }

    @Transactional
    public OrdenVendedor cancelarOrdenVendedor(Long idOrdenVendedor, Usuario vendedor) {
        validarVendedor(vendedor);
        OrdenVendedor ordenVendedor = ordenVendedorRepository.findById(idOrdenVendedor)
                .orElseThrow(OrdenVendedorNoEncontradaException::new);

        if (!ordenVendedor.getVendedor().getId().equals(vendedor.getId())) {
            throw new AccionNoPermitidaException();
        }

        if (ordenVendedor.getEstado() == EstadoOrdenVendedor.CANCELADA) {
            return ordenVendedor;
        }

        // Si el comprador ya pagó, devolver el stock lo dejaría cobrado sin libros
        if (ordenVendedor.getOrden().getEstadoPago() == EstadoPago.SIMULADO_APROBADO) {
            throw new VentaPagadaNoCancelableException();
        }

        devolverStock(ordenItemRepository.findByOrdenIdAndVendedorId(
                ordenVendedor.getOrden().getId(), vendedor.getId()));
        ordenVendedor.setEstado(EstadoOrdenVendedor.CANCELADA);
        return ordenVendedorRepository.save(ordenVendedor);
    }

    @Transactional
    public Orden cancelarOrden(Long idOrden, Usuario comprador) {
        Orden orden = ordenRepository.findByIdConCandado(idOrden)
                .orElseThrow(OrdenNoEncontradaException::new);

        if (orden.getComprador() == null || !orden.getComprador().getId().equals(comprador.getId())) {
            throw new AccionNoPermitidaException();
        }
        if (orden.getEstadoPago() != EstadoPago.PENDIENTE) {
            throw new OrdenNoCancelableException();
        }

        cancelarReserva(orden, EstadoPago.CANCELADO);
        return orden;
    }

    @Transactional
    public boolean liberarReservaVencida(Long idOrden) {
        return ordenRepository.findByIdConCandado(idOrden)
                .map(orden -> {
                    if (orden.getEstadoPago() != EstadoPago.PENDIENTE
                            || orden.getReservaHasta() == null
                            || orden.getReservaHasta().isAfter(LocalDateTime.now())) {
                        return false;
                    }
                    cancelarReserva(orden, EstadoPago.VENCIDO);
                    return true;
                })
                .orElse(false);
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void liberarReservasVencidas() {
        List<Orden> vencidas = ordenRepository.findByEstadoPagoAndReservaHastaLessThanEqual(
                EstadoPago.PENDIENTE, LocalDateTime.now());
        for (Orden orden : vencidas) {
            liberarReservaVencida(orden.getId());
        }
    }

    private void resolverVencimiento(Orden orden) {
        if (orden != null
                && orden.getEstadoPago() == EstadoPago.PENDIENTE
                && orden.getReservaHasta() != null
                && !orden.getReservaHasta().isAfter(LocalDateTime.now())) {
            liberarReservaVencida(orden.getId());
        }
    }

    private void cancelarReserva(Orden orden, EstadoPago estadoFinal) {
        List<OrdenVendedor> ordenesVendedor = ordenVendedorRepository.findByOrdenId(orden.getId());
        for (OrdenVendedor ordenVendedor : ordenesVendedor) {
            if (ordenVendedor.getEstado() == EstadoOrdenVendedor.ACTIVA) {
                devolverStock(ordenItemRepository.findByOrdenIdAndVendedorId(
                        orden.getId(), ordenVendedor.getVendedor().getId()));
                ordenVendedor.setEstado(EstadoOrdenVendedor.CANCELADA);
            }
        }
        orden.setEstadoPago(estadoFinal);
        ordenRepository.save(orden);
    }

    private void devolverStock(List<OrdenItem> items) {
        for (OrdenItem item : items) {
            libroRepository.findByIdConCandado(item.getLibro().getId()).ifPresent(libro -> {
                libro.setStock(libro.getStock() + item.getCantidad());
                // Espejo del incremento en CarritoServiceImpl.checkout: si la venta se cancela o
                // vence, esas unidades dejan de contar como "vendidas" para el ranking de bestsellers.
                int vendidosActuales = libro.getVendidos() != null ? libro.getVendidos() : 0;
                libro.setVendidos(Math.max(0, vendidosActuales - item.getCantidad()));
                libroRepository.save(libro);
            });
        }
    }

    private void validarVendedor(Usuario vendedor) {
        if (vendedor.getRol() != Rol.VENDEDOR) {
            throw new RolInvalidoException();
        }
    }
}