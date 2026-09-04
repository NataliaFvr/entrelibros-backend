package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.Pago;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoCancelableException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;

public interface PagoService {

    List<Pago> getPagos();

    Pago getPagoById(Usuario comprador, Long idPago)
            throws PagoNoEncontradoException, AccionNoPermitidaException;

    List<Pago> getPagosByOrden(Usuario comprador, Long idOrden)
            throws OrdenNoEncontradaException, AccionNoPermitidaException;

    Pago crearPago(Usuario comprador, Long idOrden, String proveedor)
            throws OrdenNoEncontradaException, AccionNoPermitidaException, OrdenNoCancelableException;
}
