package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.ResenaVendedor;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CalificacionInvalidaException;
import com.uade.entrelibros.backend.exceptions.EnvioItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ResenaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.ResenaVendedorNoEncontradaException;

public interface ResenaVendedorService {

    List<ResenaVendedor> getResenas();

    ResenaVendedor getResenaById(Long idResena) throws ResenaVendedorNoEncontradaException;

    List<ResenaVendedor> getResenasByVendedor(Long idVendedor);

    ResenaVendedor crearResena(Usuario comprador, Long idEnvioItem, Integer clasificacion, String comentario)
            throws EnvioItemNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException,
            AccionNoPermitidaException;
}
