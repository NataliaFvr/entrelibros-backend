package com.uade.entrelibros.backend.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.EstadoModeracion;
import com.uade.entrelibros.backend.entity.ImagenLibro;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.TipoNotificacion;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.ImagenLibroNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;
import com.uade.entrelibros.backend.exceptions.UltimaFotoException;
import com.uade.entrelibros.backend.repository.ImagenLibroRepository;
import com.uade.entrelibros.backend.repository.LibroRepository;

@Service
public class ImagenLibroServiceImpl implements ImagenLibroService {

    @Autowired
    private ImagenLibroRepository imagenLibroRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private NotificacionService notificacionService;

    public List<ImagenLibro> getImagenesByLibroId(Long libroId) throws LibroNoEncontradoException {
        libroRepository.findById(libroId).orElseThrow(LibroNoEncontradoException::new);
        List<ImagenLibro> imagenes = imagenLibroRepository.findByLibroId(libroId);
        if (imagenes.isEmpty()) {
            throw new ListaVaciaException("Ese libro no tiene imágenes cargadas");
        }
        return imagenes;
    }

    public ImagenLibro getImagenById(Long imagenId) throws ImagenLibroNoEncontradaException {
        return imagenLibroRepository.findById(imagenId)
                .orElseThrow(ImagenLibroNoEncontradaException::new);
    }

    @Override
    @Transactional
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

        // Una foto nueva vuelve a revision (igual que editar los datos): el admin tiene que ver lo que se publica.
        // Un libro nuevo ya esta EN_REVISION, asi que publicar con fotos no notifica dos veces.
        if (libro.getEstadoModeracion() != EstadoModeracion.EN_REVISION) {
            libro.guardarSnapshotActual();
            libro.setEstadoModeracion(EstadoModeracion.EN_REVISION);
            libroRepository.save(libro);
            notificacionService.notificarAdmins(TipoNotificacion.LIBRO_PENDIENTE_REVISION,
                    vendedor.getNombre() + " " + vendedor.getApellido() + " cambio las fotos del libro \""
                            + libro.getTitulo() + "\" para su revision", libro);
        }

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

        // Un libro aprobado (visible en el catalogo) no se puede quedar sin fotos. El front sube las nuevas
        // antes de borrar las viejas, asi que ese flujo no se ve afectado.
        Libro libro = imagen.getLibro();
        if (libro.getEstadoModeracion() == EstadoModeracion.ACEPTADO
                && imagenLibroRepository.countByLibroId(libro.getId()) <= ImagenValidator.MIN_FOTOS_POR_LIBRO) {
            throw new UltimaFotoException();
        }
        imagenLibroRepository.delete(imagen);
    }
}