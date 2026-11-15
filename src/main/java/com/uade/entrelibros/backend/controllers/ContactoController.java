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

// Formulario "Contáctanos": manda un mail real al equipo. Los datos adicionales (teléfono, departamento)
// llegan en campos propios y el texto del mail lo arma el back.
@RestController
@RequestMapping("contacto")
public class ContactoController {

    @Autowired
    private EmailService emailService;

    // La casilla de contacto puede sobrescribirse sin cambiar la cuenta SMTP que envía los mensajes.
    @Value("${app.contacto.email:entrelibross28@gmail.com}")
    private String emailDestino;

    @PostMapping
    public ResponseEntity<Map<String, String>> enviarContacto(@Valid @RequestBody ContactoRequest request) {
        emailService.enviarEmail(
                emailDestino,
                "Nuevo mensaje de contacto - EntreLibros",
                armarTexto(request));
        return ResponseEntity.ok(Map.of("mensaje", "Mensaje enviado correctamente"));
    }

    private String armarTexto(ContactoRequest request) {
        StringBuilder texto = new StringBuilder();
        texto.append("De: ").append(request.getNombre()).append(' ').append(request.getApellido())
                .append(" (").append(request.getEmail()).append(")\n");
        String telefono = ((request.getArea() == null ? "" : request.getArea()) + " "
                + (request.getTelefono() == null ? "" : request.getTelefono())).trim();
        if (!telefono.isEmpty()) {
            texto.append("Teléfono: ").append(telefono).append('\n');
        }
        if (request.getDepartamento() != null) {
            texto.append("Departamento: ").append(request.getDepartamento()).append('\n');
        }
        return texto.append('\n').append(request.getMensaje()).toString();
    }
}