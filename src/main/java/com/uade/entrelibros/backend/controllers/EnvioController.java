package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Envio;
import com.uade.entrelibros.backend.entity.dto.EnvioRequest;
import com.uade.entrelibros.backend.exceptions.EnvioDuplicadoException;
import com.uade.entrelibros.backend.exceptions.EnvioNoEncontradoException;
import com.uade.entrelibros.backend.service.EnvioService;

@RestController
@RequestMapping("envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @GetMapping
    public ResponseEntity<List<Envio>> getEnvios() {
        return ResponseEntity.ok(envioService.getEnvios());
    }

    @GetMapping("/{idEnvio}")
    public ResponseEntity<Envio> getEnvioById(@PathVariable Long idEnvio)
            throws EnvioNoEncontradoException {
        return ResponseEntity.ok(envioService.getEnvioById(idEnvio));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Envio> crearEnvio(@RequestBody EnvioRequest request)
            throws EnvioDuplicadoException {
        Envio result = envioService.crearEnvio(request.getZona(), request.getCostoFijo());
        return ResponseEntity.created(URI.create("/envios/" + result.getId())).body(result);
    }
}
