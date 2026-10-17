package com.uade.entrelibros.backend.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.uade.entrelibros.backend.exceptions.CampoInvalidoException;
import com.uade.entrelibros.backend.exceptions.CamposInvalidosException;
import com.uade.entrelibros.backend.exceptions.EntreLibrosException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.CuentaDadaDeBajaException;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
        // Un campo puede incumplir varias reglas a la vez (ej.: vacio rompe @NotBlank, @Size y @Pattern):
        // si falta el dato se informa eso ("obligatorio"); si no, el primer mensaje que llegue.
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            boolean obligatorio = "NotBlank".equals(error.getCode()) || "NotNull".equals(error.getCode());
            if (obligatorio) {
                errores.put(error.getField(), error.getDefaultMessage());
            } else {
                errores.putIfAbsent(error.getField(), error.getDefaultMessage());
            }
        });
        return ResponseEntity.badRequest().body(errores);
    }

    // Validacion de un solo campo lanzada desde un service: mismo formato { campo: mensaje } que @Valid
    @ExceptionHandler(CamposInvalidosException.class)
    public ResponseEntity<Map<String, String>> handleCamposInvalidos(CamposInvalidosException ex) {
        return ResponseEntity.badRequest().body(ex.getErrores());
    }

    @ExceptionHandler(CampoInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleCampoInvalido(CampoInvalidoException ex) {
        return ResponseEntity.badRequest().body(Map.of(ex.getCampo(), ex.getMensaje()));
    }

    // Red de seguridad: un dato que la base rechaza (largo de columna, restriccion) es un error del pedido, no un 500
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleIntegridadDeDatos(DataIntegrityViolationException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Alguno de los datos supera el largo permitido o no es válido"));
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

    @ExceptionHandler(CuentaDadaDeBajaException.class)
    public ResponseEntity<Map<String, String>> handleCuentaDadaDeBaja(CuentaDadaDeBajaException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "error", "La cuenta está dada de baja",
                        "codigo", "cuenta_dada_de_baja"));
    }
}