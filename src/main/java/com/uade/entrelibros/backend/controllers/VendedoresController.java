package com.uade.entrelibros.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.EstadisticasVendedorResponse;
import com.uade.entrelibros.backend.entity.dto.VendedorPerfilResponse;
import com.uade.entrelibros.backend.service.EstadisticasVendedorService;
import com.uade.entrelibros.backend.service.VendedorPerfilService;

@RestController
@RequestMapping("vendedores")
public class VendedoresController {

    @Autowired
    private EstadisticasVendedorService estadisticasService;

    @Autowired
    private VendedorPerfilService vendedorPerfilService;

    @GetMapping("/{idVendedor}")
    public ResponseEntity<VendedorPerfilResponse> getPerfil(@PathVariable Long idVendedor) {
        return ResponseEntity.ok(vendedorPerfilService.getPerfil(idVendedor));
    }

    @PreAuthorize("hasAuthority('VENDEDOR')")
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasVendedorResponse> getEstadisticas(@AuthenticationPrincipal Usuario vendedor) {
        return ResponseEntity.ok(estadisticasService.getEstadisticas(vendedor));
    }

}