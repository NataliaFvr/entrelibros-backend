package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.EstadoPago;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.Pago;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.OrdenNoPagableException;
import com.uade.entrelibros.backend.repository.OrdenItemRepository;
import com.uade.entrelibros.backend.repository.OrdenRepository;
import com.uade.entrelibros.backend.repository.PagoRepository;

@Service
public class PagoServiceImpl implements PagoService {

    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private OrdenRepository ordenRepository;
    @Autowired
    private OrdenItemRepository ordenItemRepository;
    @Autowired
    private OrdenService ordenService;

    public List<Pago> getPagos() {
        List<Pago> pagos = pagoRepository.findAll();
        if (pagos.isEmpty()) {
            throw new ListaVaciaException("No hay pagos registrados");
        }
        return pagos;
    }
    public Pago getPagoById(Usuario comprador, Long idPago) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(PagoNoEncontradoException::new);
        validarComprador(pago.getOrden(), comprador);
        return pago;
    }

    public List<Pago> getPagosByOrden(Usuario comprador, Long idOrden) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(OrdenNoEncontradaException::new);
        validarComprador(orden, comprador);
        List<Pago> pagos = pagoRepository.findByOrdenId(idOrden);
        if (pagos.isEmpty()) {
            throw new ListaVaciaException("Esa orden no tiene pagos registrados");
        }
        return pagos;
    }

    @Transactional
    public Pago crearPago(Usuario comprador, Long idOrden, String proveedor) {
        if (ordenService.liberarReservaVencida(idOrden)) {
            throw new OrdenNoPagableException();
        }

        Orden orden = ordenRepository.findByIdConCandado(idOrden)
                .orElseThrow(OrdenNoEncontradaException::new);

        validarComprador(orden, comprador);
        if (orden.getEstadoPago() != EstadoPago.PENDIENTE) {
            throw new OrdenNoPagableException();
        }

        // Pago simulado: se aprueba automaticamente y se refleja el estado en la orden
        Pago pago = new Pago(orden, proveedor);
        pago = pagoRepository.save(pago);

        orden.setEstadoPago(pago.getResultado());
        ordenRepository.save(orden);

        return pago;
    }

    // Nuevo: trae los OrdenItem de la orden pagada, para que el controller arme
    // el PagoResponse con los idOrdenItem y el frontend sepa qué puede reseñar.
    public List<OrdenItem> getItemsDeOrdenPagada(Long idOrden) {
        return ordenItemRepository.findByOrdenId(idOrden);
    }

    private void validarComprador(Orden orden, Usuario comprador) {
        if (orden == null || orden.getComprador() == null || comprador == null
                || !orden.getComprador().getId().equals(comprador.getId())) {
            throw new AccionNoPermitidaException();
        }
    }
}