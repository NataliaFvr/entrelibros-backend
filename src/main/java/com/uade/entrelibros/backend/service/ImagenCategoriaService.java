package com.uade.entrelibros.backend.service;

import java.io.IOException;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.uade.entrelibros.backend.entity.ImagenCategoria;
import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.ImagenCategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;

public interface ImagenCategoriaService {

    ImagenCategoria getImagenByCategoriaId(Long categoriaId)
            throws CategoriaNoEncontradaException, ImagenCategoriaNoEncontradaException;

    boolean tieneImagen(Long categoriaId);

    Set<Long> getIdsCategoriasConImagen();

    ImagenCategoria createImagenCategoria(Long categoriaId, MultipartFile archivo)
            throws CategoriaNoEncontradaException, ArchivoDemasiadoGrandeException,
            TipoArchivoNoPermitidoException, IOException;

    // Soft delete: marca la imagen como inactiva, no borra la fila
    void deleteImagenCategoria(Long categoriaId)
            throws CategoriaNoEncontradaException, ImagenCategoriaNoEncontradaException;
}
