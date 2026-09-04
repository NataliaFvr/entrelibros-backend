package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.EstadoPago;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.Pago;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.OrdenNoCancelableException;
import com.uade.entrelibros.backend.repository.OrdenRepository;
import com.uade.entrelibros.backend.repository.PagoRepository;

@Service
public class PagoServiceImpl implements PagoService {

    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private OrdenRepository ordenRepository;
    @Autowired
    private OrdenService ordenService;

    public List<Pago> getPagos() {
        return pagoRepository.findAll();
    }

    public Pago getPagoById(Usuario comprador, Long idPago)
            throws PagoNoEncontradoException, AccionNoPermitidaException {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(PagoNoEncontradoException::new);
        validarComprador(pago.getOrden(), comprador);
        return pago;
    }

    public List<Pago> getPagosByOrden(Usuario comprador, Long idOrden)
            throws OrdenNoEncontradaException, AccionNoPermitidaException {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(OrdenNoEncontradaException::new);
        validarComprador(orden, comprador);
        return pagoRepository.findByOrdenId(idOrden);
    }

    @Transactional
    public Pago crearPago(Usuario comprador, Long idOrden, String proveedor)
            throws OrdenNoEncontradaException, AccionNoPermitidaException, OrdenNoCancelableException {
        if (ordenService.liberarReservaVencida(idOrden)) {
            throw new OrdenNoCancelableException();
        }

        Orden orden = ordenRepository.findByIdConCandado(idOrden)
                .orElseThrow(OrdenNoEncontradaException::new);

        validarComprador(orden, comprador);
        if (orden.getEstadoPago() != EstadoPago.PENDIENTE) {
            throw new OrdenNoCancelableException();
        }

        // Pago simulado: se aprueba automaticamente y se refleja el estado en la orden
        Pago pago = new Pago(orden, proveedor);
        pago = pagoRepository.save(pago);

        orden.setEstadoPago(pago.getResultado());
        ordenRepository.save(orden);

        return pago;
    }

    private void validarComprador(Orden orden, Usuario comprador) throws AccionNoPermitidaException {
        if (orden == null || orden.getComprador() == null || comprador == null
                || !orden.getComprador().getId().equals(comprador.getId())) {
            throw new AccionNoPermitidaException();
        }
    }
}
