package com.uade.entrelibros.backend.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.uade.entrelibros.backend.entity.Categoria;
import com.uade.entrelibros.backend.entity.ImagenCategoria;
import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.ImagenCategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;
import com.uade.entrelibros.backend.repository.CategoriaRepository;
import com.uade.entrelibros.backend.repository.ImagenCategoriaRepository;

@Service
public class ImagenCategoriaServiceImpl implements ImagenCategoriaService {

    @Autowired
    private ImagenCategoriaRepository imagenCategoriaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public ImagenCategoria getImagenByCategoriaId(Long categoriaId)
            throws CategoriaNoEncontradaException, ImagenCategoriaNoEncontradaException {
        categoriaRepository.findById(categoriaId).orElseThrow(CategoriaNoEncontradaException::new);
        return imagenCategoriaRepository.findActivaByCategoriaId(categoriaId)
                .orElseThrow(ImagenCategoriaNoEncontradaException::new);
    }

    @Override
    public boolean tieneImagen(Long categoriaId) {
        return imagenCategoriaRepository.findActivaByCategoriaId(categoriaId).isPresent();
    }

    @Override
    public Set<Long> getIdsCategoriasConImagen() {
        return new HashSet<>(imagenCategoriaRepository.findIdsCategoriasConImagenActiva());
    }

    // @Transactional: si ya habia una fila (activa o dada de baja) se pisa y se reactiva,
    // asi queda siempre una sola fila por categoria
    @Override
    @Transactional
    public ImagenCategoria createImagenCategoria(Long categoriaId, MultipartFile archivo)
            throws CategoriaNoEncontradaException, ArchivoDemasiadoGrandeException,
            TipoArchivoNoPermitidoException, IOException {

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(CategoriaNoEncontradaException::new);

        ImagenValidator.validar(archivo);

        byte[] bytes = archivo.getBytes();

        ImagenCategoria imagen = imagenCategoriaRepository.findByCategoriaId(categoriaId).orElse(null);
        if (imagen == null) {
            imagen = new ImagenCategoria(bytes, archivo.getContentType(), categoria);
        } else {
            imagen.setImagen(bytes);
            imagen.setTipoContenido(archivo.getContentType());
            imagen.setActiva(true);
            imagen.setFechaActualizacion(LocalDateTime.now());
        }

        return imagenCategoriaRepository.save(imagen);
    }

    @Override
    @Transactional
    public void deleteImagenCategoria(Long categoriaId)
            throws CategoriaNoEncontradaException, ImagenCategoriaNoEncontradaException {

        categoriaRepository.findById(categoriaId).orElseThrow(CategoriaNoEncontradaException::new);

        ImagenCategoria imagen = imagenCategoriaRepository.findActivaByCategoriaId(categoriaId)
                .orElseThrow(ImagenCategoriaNoEncontradaException::new);

        imagen.setActiva(false);
        imagen.setFechaActualizacion(LocalDateTime.now());
        imagenCategoriaRepository.save(imagen);
    }
}
