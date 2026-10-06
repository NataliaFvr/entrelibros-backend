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

import com.uade.entrelibros.backend.entity.ImagenCategoria;
import com.uade.entrelibros.backend.entity.dto.CategoriaResponse;
import com.uade.entrelibros.backend.exceptions.ArchivoDemasiadoGrandeException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.ImagenCategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.TipoArchivoNoPermitidoException;
import com.uade.entrelibros.backend.service.ImagenCategoriaService;

@RestController
@RequestMapping("categorias/{categoriaId}/imagen")
public class ImagenesCategoriaController {

    @Autowired
    private ImagenCategoriaService imagenCategoriaService;

    // Publico (el GET de /categorias/** esta permitido en SecurityConfig).
    // Sirve los bytes directo para usarlo como src de un <img>.
    @GetMapping
    public ResponseEntity<byte[]> getImagenCategoria(@PathVariable Long categoriaId)
            throws CategoriaNoEncontradaException, ImagenCategoriaNoEncontradaException {
        ImagenCategoria imagen = imagenCategoriaService.getImagenByCategoriaId(categoriaId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imagen.getTipoContenido()))
                .body(imagen.getImagen());
    }

    // Sube la imagen. Si la categoria ya tenia una (o la habian dado de baja), la reemplaza.
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoriaResponse> createImagenCategoria(
            @PathVariable Long categoriaId,
            @RequestParam("archivo") MultipartFile archivo)
            throws CategoriaNoEncontradaException, ArchivoDemasiadoGrandeException,
            TipoArchivoNoPermitidoException, IOException {
        ImagenCategoria result = imagenCategoriaService.createImagenCategoria(categoriaId, archivo);
        return ResponseEntity.created(URI.create("/categorias/" + categoriaId + "/imagen"))
                .body(CategoriaResponse.from(result.getCategoria(), true));
    }

    // Soft delete: la imagen queda inactiva en la base y el front vuelve al circulo de color
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteImagenCategoria(@PathVariable Long categoriaId)
            throws CategoriaNoEncontradaException, ImagenCategoriaNoEncontradaException {
        imagenCategoriaService.deleteImagenCategoria(categoriaId);
        return ResponseEntity.ok(Map.of("mensaje", "Imagen de la categoría eliminada correctamente"));
    }
}
