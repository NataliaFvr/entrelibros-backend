package com.uade.entrelibros.backend.service;

import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.springframework.web.multipart.MultipartFile;

import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;

public class ImagenValidator {

    private static final long TAMANIO_MAXIMO_BYTES = 10L * 1024 * 1024; // 10MB
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
        if (ImageIO.read(archivo.getInputStream()) == null) {
            throw new TipoArchivoNoPermitidoException();
        }
    }
}