package com.uade.entrelibros.backend.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uade.entrelibros.backend.entity.ImagenLibro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.ImagenLibroNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;

public interface ImagenLibroService {
    List<ImagenLibro> getImagenesByLibroId(Long libroId) throws LibroNoEncontradoException;

    ImagenLibro getImagenById(Long imagenId) throws ImagenLibroNoEncontradaException;

    ImagenLibro createImagenLibro(Usuario vendedor, MultipartFile archivo, Integer orden, Long idLibro)
            throws LibroNoEncontradoException, ArchivoDemasiadoGrandeException,
            TipoArchivoNoPermitidoException, IOException, AccionNoPermitidaException;

    void deleteImagenLibro(Usuario vendedor, Long imagenId)
            throws ImagenLibroNoEncontradaException, AccionNoPermitidaException;
}