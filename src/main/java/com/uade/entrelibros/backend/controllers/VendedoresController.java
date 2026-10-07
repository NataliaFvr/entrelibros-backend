package com.uade.entrelibros.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.EstadisticasVendedorResponse;
import com.uade.entrelibros.backend.entity.ResenaLibro;
import com.uade.entrelibros.backend.entity.dto.ResenaLibroResponse;
import com.uade.entrelibros.backend.service.EstadisticasVendedorService;
import com.uade.entrelibros.backend.service.ResenaLibroService;
import java.util.List;

@RestController
@RequestMapping("vendedores")
public class VendedoresController {

    @Autowired
    private EstadisticasVendedorService estadisticasService;

    @Autowired
    private ResenaLibroService resenaLibroService;

    @PreAuthorize("hasAuthority('VENDEDOR')")
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasVendedorResponse> getEstadisticas(@AuthenticationPrincipal Usuario vendedor) {
        return ResponseEntity.ok(estadisticasService.getEstadisticas(vendedor));
    }

    @GetMapping("/{idVendedor}/opiniones-libros")
    public ResponseEntity<List<ResenaLibroResponse>> getOpinionesLibros(
            @PathVariable Long idVendedor) {
        List<ResenaLibroResponse> resultado = resenaLibroService
                .getResenasByVendedor(idVendedor).stream()
                .map(ResenaLibroResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }
}