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
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.OrdenVendedorNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoCancelableException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
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

    public List<Orden> getOrdenes(Usuario usuario) {
        if (usuario.getRol() != Rol.ADMIN) {
            throw new AccionNoPermitidaException();
        }
        return ordenRepository.findAll();
    }

    public Orden getOrdenById(Long idOrden, Usuario usuario) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(OrdenNoEncontradaException::new);

        boolean esComprador = orden.getComprador().getId().equals(usuario.getId());
        boolean esVendedorDeLaOrden = ordenVendedorRepository.findByOrdenIdAndVendedorId(idOrden, usuario.getId())
                .isPresent();

        if (usuario.getRol() != Rol.ADMIN && !esComprador && !esVendedorDeLaOrden) {
            throw new AccionNoPermitidaException();
        }

        return orden;
    }

    public List<Orden> getOrdenesByComprador(Usuario comprador) {
        return ordenRepository.findByCompradorId(comprador.getId());
    }

    public List<OrdenVendedor> getOrdenesDelVendedor(Usuario vendedor) {
        validarVendedor(vendedor);
        return ordenVendedorRepository.findByVendedorId(vendedor.getId());
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
