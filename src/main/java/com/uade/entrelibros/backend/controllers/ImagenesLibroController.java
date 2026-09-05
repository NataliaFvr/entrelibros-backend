package com.uade.entrelibros.backend.controllers;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.uade.entrelibros.backend.entity.ImagenLibro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.ImagenLibroNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;
import com.uade.entrelibros.backend.service.ImagenLibroService;

@RestController
@RequestMapping("imagenes-libro")
public class ImagenesLibroController {

    @Autowired
    private ImagenLibroService imagenLibroService;

    @GetMapping("/libro/{libroId}")
    public ResponseEntity<List<ImagenLibro>> getImagenesByLibroId(@PathVariable Long libroId)
            throws LibroNoEncontradoException {
        return ResponseEntity.ok(imagenLibroService.getImagenesByLibroId(libroId));
    }

    @GetMapping("/{imagenId}")
    public ResponseEntity<ImagenLibro> getImagenById(@PathVariable Long imagenId)
            throws ImagenLibroNoEncontradaException {
        return ResponseEntity.ok(imagenLibroService.getImagenById(imagenId));
    }

    @PreAuthorize("hasAuthority('VENDEDOR')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImagenLibro> createImagenLibro(
            @AuthenticationPrincipal Usuario vendedor,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam Integer orden,
            @RequestParam Long idLibro)
            throws LibroNoEncontradoException, ArchivoDemasiadoGrandeException,
            TipoArchivoNoPermitidoException, IOException, AccionNoPermitidaException {
        ImagenLibro result = imagenLibroService.createImagenLibro(vendedor, archivo, orden, idLibro);
        return ResponseEntity.created(URI.create("/imagenes-libro/" + result.getId())).body(result);
    }

    @PreAuthorize("hasAuthority('VENDEDOR')")
    @DeleteMapping("/{imagenId}")
    public ResponseEntity<Void> deleteImagenLibro(
            @AuthenticationPrincipal Usuario vendedor,
            @PathVariable Long imagenId)
            throws ImagenLibroNoEncontradaException, AccionNoPermitidaException {
        imagenLibroService.deleteImagenLibro(vendedor, imagenId);
        return ResponseEntity.noContent().build();
    }
}