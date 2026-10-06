package com.uade.entrelibros.backend.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.uade.entrelibros.backend.entity.ImagenUsuario;
import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.ImagenUsuarioNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;
import com.uade.entrelibros.backend.exceptions.UsuarioNoEncontradoException;

public interface ImagenUsuarioService {

    ImagenUsuario getFotoByUsuarioId(Long usuarioId)
            throws UsuarioNoEncontradoException, ImagenUsuarioNoEncontradaException;

    ImagenUsuario createFotoUsuario(Long usuarioId, MultipartFile archivo)
            throws UsuarioNoEncontradoException, ArchivoDemasiadoGrandeException,
            TipoArchivoNoPermitidoException, IOException;

    // Soft delete: marca la foto como inactiva y el usuario vuelve a su avatar por defecto del front
    void deleteFotoUsuario(Long usuarioId)
            throws UsuarioNoEncontradoException, ImagenUsuarioNoEncontradaException;
}
