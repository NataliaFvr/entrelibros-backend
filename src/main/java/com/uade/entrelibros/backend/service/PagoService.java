package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.Pago;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;

public interface PagoService {

    List<Pago> getPagos();

    Pago getPagoById(Long idPago) throws PagoNoEncontradoException;

    List<Pago> getPagosByOrden(Long idOrden);

    Pago crearPago(Long idOrden, String proveedor) throws OrdenNoEncontradaException;
}
