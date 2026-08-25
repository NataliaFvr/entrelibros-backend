package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.ImagenLibro;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.exceptions.ImagenLibroNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.repository.ImagenLibroRepository;
import com.uade.entrelibros.backend.repository.LibroRepository;

@Service
public class ImagenLibroServiceImpl implements ImagenLibroService {

    @Autowired
    private ImagenLibroRepository imagenLibroRepository;

    @Autowired
    private LibroRepository libroRepository;

    public List<ImagenLibro> getImagenesByLibroId(Long libroId) throws LibroNoEncontradoException {
        libroRepository.findById(libroId).orElseThrow(LibroNoEncontradoException::new);
        return imagenLibroRepository.findByLibroId(libroId);
    }

    public ImagenLibro getImagenById(Long imagenId) throws ImagenLibroNoEncontradaException {
        return imagenLibroRepository.findById(imagenId)
                .orElseThrow(ImagenLibroNoEncontradaException::new);
    }

    public ImagenLibro createImagenLibro(String url, Integer orden, Long idLibro)
            throws LibroNoEncontradoException {
        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(LibroNoEncontradoException::new);
        return imagenLibroRepository.save(new ImagenLibro(url, orden, libro));
    }
}