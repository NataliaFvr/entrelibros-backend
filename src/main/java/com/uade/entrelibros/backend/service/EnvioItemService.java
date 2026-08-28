package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.EnvioItem;
import com.uade.entrelibros.backend.entity.ZonaEnvio;
import com.uade.entrelibros.backend.exceptions.EnvioItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.EnvioNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.OrdenVendedorNoEncontradaException;

public interface EnvioItemService {

    List<EnvioItem> getEnvioItems();

    EnvioItem getEnvioItemById(Long idEnvioItem) throws EnvioItemNoEncontradoException;

    List<EnvioItem> getEnvioItemsByOrdenVendedor(Long idOrdenVendedor);

    EnvioItem crearEnvioItem(Long idOrdenVendedor, ZonaEnvio zona)
            throws OrdenVendedorNoEncontradaException, EnvioNoEncontradoException;
}
