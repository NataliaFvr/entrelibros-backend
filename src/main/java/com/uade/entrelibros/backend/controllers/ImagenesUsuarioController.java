package com.uade.entrelibros.backend.controllers;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.uade.entrelibros.backend.entity.ImagenUsuario;
import com.uade.entrelibros.backend.entity.dto.UsuarioResponse;
import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.ImagenUsuarioNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;
import com.uade.entrelibros.backend.exceptions.UsuarioNoEncontradoException;
import com.uade.entrelibros.backend.service.ImagenUsuarioService;

@RestController
@RequestMapping("usuarios/{usuarioId}/foto")
public class ImagenesUsuarioController {

    @Autowired
    private ImagenUsuarioService imagenUsuarioService;

    // Publico (permitido en SecurityConfig): un <img> no puede mandar el token.
    // Si el usuario no tiene foto responde 404 y el front muestra su avatar por defecto.
    @GetMapping
    public ResponseEntity<byte[]> getFotoUsuario(@PathVariable Long usuarioId)
            throws UsuarioNoEncontradoException, ImagenUsuarioNoEncontradaException {
        ImagenUsuario foto = imagenUsuarioService.getFotoByUsuarioId(usuarioId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(foto.getTipoContenido()))
                .body(foto.getImagen());
    }

    // Solo el dueño de la cuenta o un ADMIN. Si ya habia una foto (o la habian quitado), la reemplaza.
    @PreAuthorize("hasAuthority('ADMIN') or #usuarioId == authentication.principal.id")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioResponse> createFotoUsuario(
            @PathVariable Long usuarioId,
            @RequestParam("archivo") MultipartFile archivo)
            throws UsuarioNoEncontradoException, ArchivoDemasiadoGrandeException,
            TipoArchivoNoPermitidoException, IOException {
        ImagenUsuario result = imagenUsuarioService.createFotoUsuario(usuarioId, archivo);
        return ResponseEntity.created(URI.create("/usuarios/" + usuarioId + "/foto"))
                .body(UsuarioResponse.from(result.getUsuario()));
    }

    // Soft delete: la foto queda inactiva en la base y el front vuelve al avatar por defecto
    @PreAuthorize("hasAuthority('ADMIN') or #usuarioId == authentication.principal.id")
    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteFotoUsuario(@PathVariable Long usuarioId)
            throws UsuarioNoEncontradoException, ImagenUsuarioNoEncontradaException {
        imagenUsuarioService.deleteFotoUsuario(usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Foto eliminada correctamente"));
    }
}
