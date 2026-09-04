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
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.web.multipart.MultipartFile;
import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public ImagenLibro createImagenLibro(MultipartFile archivo, Integer orden, Long idLibro)
            throws LibroNoEncontradoException, ArchivoDemasiadoGrandeException,
            TipoArchivoNoPermitidoException, IOException {

        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(LibroNoEncontradoException::new);

        ImagenValidator.validar(archivo);

        String extension = obtenerExtension(archivo.getOriginalFilename());
        String nombreArchivo = UUID.randomUUID() + extension;

        Path destino = Paths.get(uploadDir);
        Files.createDirectories(destino);
        Files.copy(archivo.getInputStream(), destino.resolve(nombreArchivo));

        String url = "/imagenes/" + nombreArchivo;
        return imagenLibroRepository.save(new ImagenLibro(url, orden, libro));
    }

    private String obtenerExtension(String nombreOriginal) {
        if (nombreOriginal == null || !nombreOriginal.contains(".")) return "";
        return nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
    }
}