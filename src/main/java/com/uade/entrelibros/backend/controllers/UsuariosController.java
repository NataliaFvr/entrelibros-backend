package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.UsuarioRequest;
import com.uade.entrelibros.backend.exceptions.UsuarioDuplicadoException;
import com.uade.entrelibros.backend.service.UsuarioService;

@RestController
@RequestMapping("usuarios")
public class UsuariosController {

    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<Usuario>> getUsuarios(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null)
            return ResponseEntity.ok(usuarioService.getUsuarios(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(usuarioService.getUsuarios(PageRequest.of(page, size)));
    }

    @PreAuthorize("hasAuthority('ADMIN') or #usuarioId == authentication.principal.id")
    @GetMapping("/{usuarioId}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long usuarioId) {
        Optional<Usuario> result = usuarioService.getUsuarioById(usuarioId);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Object> createUsuario(@RequestBody UsuarioRequest usuarioRequest)
            throws UsuarioDuplicadoException {
        Usuario result = usuarioService.createUsuario(
                usuarioRequest.getNombreUsuario(),
                usuarioRequest.getEmail(),
                usuarioRequest.getContrasena(),
                usuarioRequest.getNombre(),
                usuarioRequest.getApellido());
        return ResponseEntity.created(URI.create("/usuarios/" + result.getId())).body(result);
    }
}