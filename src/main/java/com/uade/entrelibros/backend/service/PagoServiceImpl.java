package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.Pago;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;
import com.uade.entrelibros.backend.repository.OrdenRepository;
import com.uade.entrelibros.backend.repository.PagoRepository;

@Service
public class PagoServiceImpl implements PagoService {

    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private OrdenRepository ordenRepository;

    public List<Pago> getPagos() {
        return pagoRepository.findAll();
    }

    public Pago getPagoById(Long idPago) throws PagoNoEncontradoException {
        return pagoRepository.findById(idPago)
                .orElseThrow(PagoNoEncontradoException::new);
    }

    public List<Pago> getPagosByOrden(Long idOrden) {
        return pagoRepository.findByOrdenId(idOrden);
    }

    public Pago crearPago(Long idOrden, String proveedor) throws OrdenNoEncontradaException {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(OrdenNoEncontradaException::new);

        // Pago simulado: se aprueba automaticamente y se refleja el estado en la orden
        Pago pago = new Pago(orden, proveedor);
        pago = pagoRepository.save(pago);

        orden.setEstadoPago(pago.getResultado());
        ordenRepository.save(orden);

        return pago;
    }
}
