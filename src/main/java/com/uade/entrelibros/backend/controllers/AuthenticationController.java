package com.uade.entrelibros.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.entrelibros.backend.entity.dto.AuthenticationRequest;
import com.uade.entrelibros.backend.entity.dto.AuthenticationResponse;
import com.uade.entrelibros.backend.entity.dto.RefreshTokenRequest;
import com.uade.entrelibros.backend.entity.dto.UsuarioRequest;
import com.uade.entrelibros.backend.exceptions.UsuarioDuplicadoException;
import com.uade.entrelibros.backend.service.AuthenticationService;
import jakarta.validation.Valid;

import java.util.Map;
import org.springframework.http.HttpStatus;
import com.uade.entrelibros.backend.entity.dto.VerificarEmailRequest;
import com.uade.entrelibros.backend.entity.dto.ReenviarCodigoRequest;
import com.uade.entrelibros.backend.service.UsuarioService;

import com.uade.entrelibros.backend.entity.dto.RecuperarContraseniaRequest;
import com.uade.entrelibros.backend.entity.dto.CambiarContraseniaRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

   private final AuthenticationService authenticationService;
   private final UsuarioService usuarioService; 
   
   @PostMapping("/register")
   public ResponseEntity<Map<String, String>> register(@Valid @RequestBody UsuarioRequest request)
        throws UsuarioDuplicadoException {
        authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of("mensaje", "Usuario registrado correctamente. Revisá tu email para verificar la cuenta."));
    }

    @PostMapping("/verificar-email")
    public ResponseEntity<Map<String, String>> verificarEmail(@Valid @RequestBody VerificarEmailRequest request) {
        usuarioService.verificarEmail(request.getEmail(), request.getCodigo());
        return ResponseEntity.ok(Map.of("mensaje", "Email verificado correctamente"));
    }

    @PostMapping("/reenviar-codigo-verificacion")
    public ResponseEntity<Map<String, String>> reenviarCodigo(@Valid @RequestBody ReenviarCodigoRequest request) {
        usuarioService.reenviarCodigoVerificacion(request.getEmail());
        return ResponseEntity.ok(Map.of("mensaje", "Código reenviado correctamente. Revisá tu email."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authenticationService.refresh(request));
    }
    @PostMapping("/recuperar-contrasenia")
    public ResponseEntity<Map<String, String>> recuperarContrasenia(@Valid @RequestBody RecuperarContraseniaRequest request) {
        usuarioService.solicitarResetPassword(request.getEmail());
        return ResponseEntity.ok(Map.of("mensaje", "Código de recuperación enviado al email"));
    }

    @PostMapping("/cambiar-contrasenia")
    public ResponseEntity<Map<String, String>> cambiarContrasenia(@Valid @RequestBody CambiarContraseniaRequest request) {
        usuarioService.cambiarContrasenia(request.getEmail(), request.getCodigo(), request.getNuevaContrasenia());
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
    }
}
