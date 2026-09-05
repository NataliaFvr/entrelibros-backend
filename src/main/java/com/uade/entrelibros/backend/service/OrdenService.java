package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.OrdenVendedor;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.OrdenVendedorNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.exceptions.OrdenNoCancelableException;

public interface OrdenService {

    List<Orden> getOrdenes(Usuario usuario);

    Orden getOrdenById(Long idOrden, Usuario usuario);

    List<Orden> getOrdenesByComprador(Usuario comprador);

    List<OrdenVendedor> getOrdenesDelVendedor(Usuario vendedor);

    OrdenVendedor cancelarOrdenVendedor(Long idOrdenVendedor, Usuario vendedor);

    Orden cancelarOrden(Long idOrden, Usuario comprador);

    boolean liberarReservaVencida(Long idOrden);

    void liberarReservasVencidas();
}
