package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.ImagenLibro;
import com.uade.entrelibros.backend.exceptions.ImagenLibroNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;

public interface ImagenLibroService {
    List<ImagenLibro> getImagenesByLibroId(Long libroId) throws LibroNoEncontradoException;

    ImagenLibro getImagenById(Long imagenId) throws ImagenLibroNoEncontradaException;

    ImagenLibro createImagenLibro(String url, Integer orden, Long idLibro)
            throws LibroNoEncontradoException;
}