package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.Pago;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.PagosResumenResponse;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoPagableException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;

public interface PagoService {

    List<Pago> getPagos();

    PagosResumenResponse getResumen();

    Pago getPagoById(Usuario comprador, Long idPago);

    List<Pago> getPagosByOrden(Usuario comprador, Long idOrden);

    Pago crearPago(Usuario comprador, Long idOrden, String proveedor);
}