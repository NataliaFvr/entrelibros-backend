package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;

public interface OrdenService {

    List<Orden> getOrdenes();

    Orden getOrdenById(Long idOrden) throws OrdenNoEncontradaException;

    List<Orden> getOrdenesByComprador(Long idComprador);
}