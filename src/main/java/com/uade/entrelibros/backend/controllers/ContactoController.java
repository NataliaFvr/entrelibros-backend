package com.uade.entrelibros.backend.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.entrelibros.backend.entity.dto.ContactoRequest;
import com.uade.entrelibros.backend.service.EmailService;

import jakarta.validation.Valid;

// Prioridad baja: el formulario de "Contáctanos" del front hoy solo simula el envío (un toast).
// Este endpoint es lo que le faltaría para que mande un mail real al equipo.
@RestController
@RequestMapping("contacto")
public class ContactoController {

    @Autowired
    private EmailService emailService;

    @Value("${app.contacto.email:soporte@entrelibros.com}")
    private String emailDestino;

    @PostMapping
    public ResponseEntity<Map<String, String>> enviarContacto(@Valid @RequestBody ContactoRequest request) {
        emailService.enviarEmail(
                emailDestino,
                "Nuevo mensaje de contacto - EntreLibros",
                "De: " + request.getNombre() + " (" + request.getEmail() + ")\n\n" + request.getMensaje());
        return ResponseEntity.ok(Map.of("mensaje", "Mensaje enviado correctamente"));
    }
}