package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.ImagenLibro;
import com.uade.entrelibros.backend.exceptions.ImagenLibroNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.service.ImagenLibroService;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;
import java.io.IOException;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImagenLibro> createImagenLibro(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam Integer orden,
            @RequestParam Long idLibro)
            throws LibroNoEncontradoException, ArchivoDemasiadoGrandeException,
            TipoArchivoNoPermitidoException, IOException {
        ImagenLibro result = imagenLibroService.createImagenLibro(archivo, orden, idLibro);
        return ResponseEntity.created(URI.create("/imagenes-libro/" + result.getId())).body(result);
    }
}