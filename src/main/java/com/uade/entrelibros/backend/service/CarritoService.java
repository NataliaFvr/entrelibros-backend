package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.Carrito;
import com.uade.entrelibros.backend.entity.CarritoItem;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.exceptions.CarritoVacioException;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CompraPropiaException;
import com.uade.entrelibros.backend.exceptions.ItemCarritoNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.LibroNoDisponibleException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.StockInsuficienteException;

public interface CarritoService {

    Carrito getOrCrearCarrito(Long idUsuario);

    List<CarritoItem> getItemsCarrito(Long idUsuario);

    CarritoItem agregarItem(Long idUsuario, Long idLibro, Integer cantidad)
            throws LibroNoEncontradoException, StockInsuficienteException, LibroNoDisponibleException,
            CompraPropiaException;

    void quitarItem(Long idUsuario, Long idItem) throws ItemCarritoNoEncontradoException, AccionNoPermitidaException;

    Orden checkout(Long idUsuario, String provinciaDestino)
            throws CarritoVacioException, StockInsuficienteException, LibroNoDisponibleException,
            CompraPropiaException;
}
