package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.ImagenLibro;
import com.uade.entrelibros.backend.entity.dto.ImagenLibroRequest;
import com.uade.entrelibros.backend.exceptions.ImagenLibroNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
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

    @PostMapping
    public ResponseEntity<ImagenLibro> createImagenLibro(@RequestBody ImagenLibroRequest request)
            throws LibroNoEncontradoException {
        ImagenLibro result = imagenLibroService.createImagenLibro(
                request.getUrl(), request.getOrden(), request.getIdLibro());
        return ResponseEntity.created(URI.create("/imagenes-libro/" + result.getId())).body(result);
    }
}