package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.ResenaLibro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CalificacionInvalidaException;
import com.uade.entrelibros.backend.exceptions.OrdenItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ResenaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.ResenaLibroNoEncontradaException;

public interface ResenaLibroService {

    List<ResenaLibro> getResenas();

    ResenaLibro getResenaById(Long idResena) throws ResenaLibroNoEncontradaException;

    List<ResenaLibro> getResenasByLibro(Long idLibro);

   ResenaLibro crearResena(Usuario comprador, Long idOrdenItem, Integer calificacion, String comentario)
            throws OrdenItemNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException,
            AccionNoPermitidaException;
}
