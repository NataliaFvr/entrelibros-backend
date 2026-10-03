package com.uade.entrelibros.backend.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.NotificacionResponse;
import com.uade.entrelibros.backend.service.NotificacionService;

@RestController
@RequestMapping("notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<Page<NotificacionResponse>> getNotificaciones(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<NotificacionResponse> resultado = notificacionService
                .getNotificaciones(usuario.getId(), PageRequest.of(page, size))
                .map(NotificacionResponse::from);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<Page<NotificacionResponse>> getNoLeidas(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<NotificacionResponse> resultado = notificacionService
                .getNoLeidas(usuario.getId(), PageRequest.of(page, size))
                .map(NotificacionResponse::from);
        return ResponseEntity.ok(resultado);
    }

    @PatchMapping("/marcar-leidas")
    public ResponseEntity<Map<String, Object>> marcarLeidas(@AuthenticationPrincipal Usuario usuario) {
        int cantidad = notificacionService.marcarLeidas(usuario.getId());
        return ResponseEntity.ok(Map.of(
                "mensaje", "Notificaciones marcadas como leidas",
                "cantidad", cantidad));
    }
}