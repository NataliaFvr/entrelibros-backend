package com.uade.entrelibros.backend.service;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.uade.entrelibros.backend.entity.ImagenUsuario;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.ImagenUsuarioNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;
import com.uade.entrelibros.backend.exceptions.UsuarioNoEncontradoException;
import com.uade.entrelibros.backend.repository.ImagenUsuarioRepository;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

@Service
public class ImagenUsuarioServiceImpl implements ImagenUsuarioService {

    @Autowired
    private ImagenUsuarioRepository imagenUsuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public ImagenUsuario getFotoByUsuarioId(Long usuarioId)
            throws UsuarioNoEncontradoException, ImagenUsuarioNoEncontradaException {
        usuarioRepository.findById(usuarioId).orElseThrow(UsuarioNoEncontradoException::new);
        return imagenUsuarioRepository.findActivaByUsuarioId(usuarioId)
                .orElseThrow(ImagenUsuarioNoEncontradaException::new);
    }

    // @Transactional: la foto y el flag tieneFoto del usuario se guardan juntos o no se guarda nada.
    // Si ya habia una fila (activa o dada de baja) se pisa y se reactiva.
    @Override
    @Transactional
    public ImagenUsuario createFotoUsuario(Long usuarioId, MultipartFile archivo)
            throws UsuarioNoEncontradoException, ArchivoDemasiadoGrandeException,
            TipoArchivoNoPermitidoException, IOException {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(UsuarioNoEncontradoException::new);

        ImagenValidator.validar(archivo);

        byte[] bytes = archivo.getBytes();

        ImagenUsuario foto = imagenUsuarioRepository.findByUsuarioId(usuarioId).orElse(null);
        if (foto == null) {
            foto = new ImagenUsuario(bytes, archivo.getContentType(), usuario);
        } else {
            foto.setImagen(bytes);
            foto.setTipoContenido(archivo.getContentType());
            foto.setActiva(true);
            foto.setFechaActualizacion(LocalDateTime.now());
        }
        ImagenUsuario guardada = imagenUsuarioRepository.save(foto);

        usuario.setTieneFoto(true);
        usuarioRepository.save(usuario);

        return guardada;
    }

    @Override
    @Transactional
    public void deleteFotoUsuario(Long usuarioId)
            throws UsuarioNoEncontradoException, ImagenUsuarioNoEncontradaException {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(UsuarioNoEncontradoException::new);

        ImagenUsuario foto = imagenUsuarioRepository.findActivaByUsuarioId(usuarioId)
                .orElseThrow(ImagenUsuarioNoEncontradaException::new);

        foto.setActiva(false);
        foto.setFechaActualizacion(LocalDateTime.now());
        imagenUsuarioRepository.save(foto);

        usuario.setTieneFoto(false);
        usuarioRepository.save(usuario);
    }
}
