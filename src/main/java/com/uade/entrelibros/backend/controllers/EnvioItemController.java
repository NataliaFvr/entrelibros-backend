package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.EnvioItem;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.EnvioItemRequest;
import com.uade.entrelibros.backend.entity.dto.EnvioItemResponse;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.EnvioItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.EnvioNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.OrdenVendedorNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.service.EnvioItemService;

@RestController
@RequestMapping("envio-items")
public class EnvioItemController {

    @Autowired
    private EnvioItemService envioItemService;

    @GetMapping
    public ResponseEntity<List<EnvioItemResponse>> getEnvioItems() {
        List<EnvioItemResponse> resultado = envioItemService.getEnvioItems().stream()
                .map(EnvioItemResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{idEnvioItem}")
    public ResponseEntity<EnvioItemResponse> getEnvioItemById(@PathVariable Long idEnvioItem)
            throws EnvioItemNoEncontradoException {
        EnvioItem envioItem = envioItemService.getEnvioItemById(idEnvioItem);
        return ResponseEntity.ok(EnvioItemResponse.from(envioItem));
    }

    @GetMapping("/orden-vendedor/{idOrdenVendedor}")
    public ResponseEntity<List<EnvioItemResponse>> getEnvioItemsByOrdenVendedor(@PathVariable Long idOrdenVendedor) {
        List<EnvioItemResponse> resultado = envioItemService.getEnvioItemsByOrdenVendedor(idOrdenVendedor).stream()
                .map(EnvioItemResponse::from)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @PreAuthorize("hasAuthority('VENDEDOR')")
    @PostMapping
    public ResponseEntity<EnvioItemResponse> crearEnvioItem(
            @AuthenticationPrincipal Usuario vendedor,
            @RequestBody EnvioItemRequest request)
            throws OrdenVendedorNoEncontradaException, EnvioNoEncontradoException,
            AccionNoPermitidaException, RolInvalidoException {
        EnvioItem result = envioItemService.crearEnvioItem(vendedor, request.getIdOrdenVendedor(), request.getZona());
        return ResponseEntity.created(URI.create("/envio-items/" + result.getId()))
                .body(EnvioItemResponse.from(result));
    }
}
