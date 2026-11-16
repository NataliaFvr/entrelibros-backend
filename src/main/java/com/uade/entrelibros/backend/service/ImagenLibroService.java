package com.uade.entrelibros.backend.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    // La primera foto (la portada) de un libro, o null si no tiene
    ImagenLibro getPortada(Long idLibro);

    // Id de la portada de cada libro (idLibro -> idImagen), en una sola consulta para toda la lista
    Map<Long, Long> getIdsPortadas(List<Long> idsLibros);
}