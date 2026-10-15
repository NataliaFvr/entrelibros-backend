package com.uade.entrelibros.backend.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.springframework.web.multipart.MultipartFile;

import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.ImagenDemasiadoChicaException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;

public class ImagenValidator {

    private static final long TAMANIO_MAXIMO_BYTES = 10L * 1024 * 1024; // 10MB

    // Lado menor minimo de cualquier imagen (libro, perfil o categoria): mas chica no se ve bien ni recortada
    public static final int LADO_MINIMO_PX = 150;
    // Cantidad de fotos de un libro publicado. Se controla al aprobar el libro (ver LibroServiceImpl)
    public static final int MIN_FOTOS_POR_LIBRO = 1;
    public static final int MAX_FOTOS_POR_LIBRO = 5;
    private static final List<String> TIPOS_PERMITIDOS = List.of("image/jpeg", "image/png");

    public static void validar(MultipartFile archivo)
            throws ArchivoDemasiadoGrandeException, TipoArchivoNoPermitidoException, IOException {

        if (archivo == null || archivo.isEmpty()) {
            throw new TipoArchivoNoPermitidoException();
        }

        if (archivo.getSize() > TAMANIO_MAXIMO_BYTES) {
            throw new ArchivoDemasiadoGrandeException();
        }

        if (archivo.getContentType() == null || !TIPOS_PERMITIDOS.contains(archivo.getContentType())) {
            throw new TipoArchivoNoPermitidoException();
        }

        // el Content-Type del header lo manda el cliente y se puede falsear
        // (renombrar un .exe a foto.jpg). Esto chequea que el contenido
        // realmente se pueda decodificar como imagen.
        BufferedImage imagen = ImageIO.read(archivo.getInputStream());
        if (imagen == null) {
            throw new TipoArchivoNoPermitidoException();
        }
        // El recorte del front ya lo avisa, pero la decision final es de aca
        if (Math.min(imagen.getWidth(), imagen.getHeight()) < LADO_MINIMO_PX) {
            throw new ImagenDemasiadoChicaException();
        }
    }
}