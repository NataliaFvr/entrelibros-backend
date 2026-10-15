package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.ResenaVendedor;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.ResenaResumenResponse;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CalificacionInvalidaException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ResenaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.ResenaVendedorNoEncontradaException;

public interface ResenaVendedorService {

    List<ResenaVendedor> getResenas();

    ResenaVendedor getResenaById(Long idResena) throws ResenaVendedorNoEncontradaException;

    List<ResenaVendedor> getResenasByVendedor(Long idVendedor);

    ResenaVendedor crearResena(Usuario comprador, Long idPago, Long idVendedor, Integer clasificacion,
            String comentario)
            throws PagoNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException,
            AccionNoPermitidaException;

    ResenaVendedor modificarResena(Long idResena, Usuario comprador, Integer clasificacion, String comentario);

    void eliminarResena(Long idResena, Usuario usuario);

    // Lo que el front necesita para habilitar (o no) el formulario de calificacion, decidido por el back
    EstadoResena getEstadoResena(Usuario usuario, Long idVendedor);

    ResenaResumenResponse getResumenByVendedor(Long idVendedor);

    record EstadoResena(boolean puedeResenar, String motivo, Long idPago, ResenaVendedor miResena) {
    }
}