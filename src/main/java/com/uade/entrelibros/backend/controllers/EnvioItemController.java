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
    public ResponseEntity<List<EnvioItem>> getEnvioItems() {
        return ResponseEntity.ok(envioItemService.getEnvioItems());
    }

    @GetMapping("/{idEnvioItem}")
    public ResponseEntity<EnvioItem> getEnvioItemById(@PathVariable Long idEnvioItem)
            throws EnvioItemNoEncontradoException {
        return ResponseEntity.ok(envioItemService.getEnvioItemById(idEnvioItem));
    }

    @GetMapping("/orden-vendedor/{idOrdenVendedor}")
    public ResponseEntity<List<EnvioItem>> getEnvioItemsByOrdenVendedor(@PathVariable Long idOrdenVendedor) {
        return ResponseEntity.ok(envioItemService.getEnvioItemsByOrdenVendedor(idOrdenVendedor));
    }

    @PreAuthorize("hasAuthority('VENDEDOR')")
    @PostMapping
    public ResponseEntity<EnvioItem> crearEnvioItem(
            @AuthenticationPrincipal Usuario vendedor,
            @RequestBody EnvioItemRequest request)
            throws OrdenVendedorNoEncontradaException, EnvioNoEncontradoException,
            AccionNoPermitidaException, RolInvalidoException {
        EnvioItem result = envioItemService.crearEnvioItem(vendedor, request.getIdOrdenVendedor(), request.getZona());
        return ResponseEntity.created(URI.create("/envio-items/" + result.getId())).body(result);
    }
}
