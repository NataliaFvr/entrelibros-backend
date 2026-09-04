package com.uade.entrelibros.backend.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.RolUpdateRequest;
import com.uade.entrelibros.backend.entity.dto.UsuarioRequest;
import com.uade.entrelibros.backend.entity.dto.UsuarioUpdateRequest;
import com.uade.entrelibros.backend.exceptions.UsuarioDuplicadoException;
import com.uade.entrelibros.backend.exceptions.UsuarioNoEncontradoException;
import com.uade.entrelibros.backend.service.UsuarioService;

import jakarta.validation.Valid;

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
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long usuarioId)
            throws UsuarioNoEncontradoException {
        Usuario result = usuarioService.getUsuarioById(usuarioId)
                .orElseThrow(UsuarioNoEncontradoException::new);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Object> createUsuario(@Valid @RequestBody UsuarioRequest usuarioRequest)
            throws UsuarioDuplicadoException {
        Usuario result = usuarioService.createUsuario(
                usuarioRequest.getNombreUsuario(),
                usuarioRequest.getEmail(),
                usuarioRequest.getContrasena(),
                usuarioRequest.getNombre(),
                usuarioRequest.getApellido(),
                usuarioRequest.getRol());
        return ResponseEntity.created(URI.create("/usuarios/" + result.getId())).body(result);
    }

    @PreAuthorize("hasAuthority('ADMIN') or #usuarioId == authentication.principal.id")
    @PatchMapping("/{usuarioId}")
    public ResponseEntity<Usuario> updateUsuario(
            @PathVariable Long usuarioId,
            @Valid @RequestBody UsuarioUpdateRequest usuarioUpdateRequest)
            throws UsuarioDuplicadoException, UsuarioNoEncontradoException {
        Usuario result = usuarioService.updateUsuario(usuarioId, usuarioUpdateRequest);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long usuarioId)
            throws UsuarioNoEncontradoException {
        usuarioService.eliminarUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{usuarioId}/rol")
    public ResponseEntity<Usuario> cambiarRol(
            @PathVariable Long usuarioId,
            @RequestBody RolUpdateRequest request)
            throws UsuarioNoEncontradoException {
        Usuario result = usuarioService.cambiarRol(usuarioId, request.getRol());
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{usuarioId}/reactivar")
    public ResponseEntity<Usuario> reactivarUsuario(@PathVariable Long usuarioId)
            throws UsuarioNoEncontradoException {
        Usuario result = usuarioService.reactivarUsuario(usuarioId);
        return ResponseEntity.ok(result);
    }
}