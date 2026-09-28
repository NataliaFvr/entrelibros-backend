package com.uade.entrelibros.backend.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.security.authentication.DisabledException;
import com.uade.entrelibros.backend.exceptions.EntreLibrosException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;

// Todas las respuestas de error de la API salen con el mismo formato: {"error": "mensaje"}
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Excepciones propias: el codigo y el mensaje salen del @ResponseStatus de cada una
    @ExceptionHandler(EntreLibrosException.class)
    public ResponseEntity<Map<String, String>> handleEntreLibrosException(EntreLibrosException ex) {
        ResponseStatus anotacion = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class);
        HttpStatus status = anotacion != null ? anotacion.code() : HttpStatus.BAD_REQUEST;
        String mensaje = anotacion != null && !anotacion.reason().isBlank() ? anotacion.reason() : "Error en la solicitud";
        return ResponseEntity
                .status(status)
                .body(Map.of("error", mensaje));
    }

    @ExceptionHandler({ BadCredentialsException.class, AuthenticationException.class })
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Email o contraseña incorrectos"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleArchivoGrande(MaxUploadSizeExceededException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "El archivo supera el tamaño máximo permitido (10MB)"));
    }

    @ExceptionHandler(ListaVaciaException.class)
    public ResponseEntity<Map<String, String>> handleListaVacia(ListaVaciaException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, String>> handleUsuarioNoVerificado(DisabledException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "error", "Tenés que verificar tu email antes de iniciar sesión",
                        "codigo", "email_no_verificado"));
    }
}
