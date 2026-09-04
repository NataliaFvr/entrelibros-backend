package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.EnvioItem;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.ZonaEnvio;
import com.uade.entrelibros.backend.exceptions.EnvioItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.EnvioNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.OrdenVendedorNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;

public interface EnvioItemService {

    List<EnvioItem> getEnvioItems();

    EnvioItem getEnvioItemById(Long idEnvioItem) throws EnvioItemNoEncontradoException;

    List<EnvioItem> getEnvioItemsByOrdenVendedor(Long idOrdenVendedor);

    EnvioItem crearEnvioItem(Usuario vendedor, Long idOrdenVendedor, ZonaEnvio zona)
            throws OrdenVendedorNoEncontradaException, EnvioNoEncontradoException,
            AccionNoPermitidaException, RolInvalidoException;
}
