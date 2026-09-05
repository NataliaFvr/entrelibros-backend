package com.uade.entrelibros.backend.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uade.entrelibros.backend.entity.ImagenLibro;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.ImagenLibroNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;
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

    @Override
    public ImagenLibro createImagenLibro(Usuario vendedor, MultipartFile archivo, Integer orden, Long idLibro)
            throws LibroNoEncontradoException, ArchivoDemasiadoGrandeException,
            TipoArchivoNoPermitidoException, IOException, AccionNoPermitidaException {

        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(LibroNoEncontradoException::new);

        if (vendedor == null || libro.getVendedor() == null
                || !libro.getVendedor().getId().equals(vendedor.getId())) {
            throw new AccionNoPermitidaException();
        }

        ImagenValidator.validar(archivo);

        byte[] bytes = archivo.getBytes();
        ImagenLibro imagenLibro = new ImagenLibro(bytes, archivo.getContentType(), orden, libro);

        return imagenLibroRepository.save(imagenLibro);
    }

    @Override
    public void deleteImagenLibro(Usuario vendedor, Long imagenId)
            throws ImagenLibroNoEncontradaException, AccionNoPermitidaException {

        ImagenLibro imagen = imagenLibroRepository.findById(imagenId)
                .orElseThrow(ImagenLibroNoEncontradaException::new);

        if (vendedor == null || imagen.getLibro().getVendedor() == null
                || !imagen.getLibro().getVendedor().getId().equals(vendedor.getId())) {
            throw new AccionNoPermitidaException();
        }

        imagenLibroRepository.delete(imagen);
    }
}