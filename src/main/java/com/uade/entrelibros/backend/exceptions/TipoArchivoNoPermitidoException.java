package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Solo se permiten imágenes JPG o PNG")
public class TipoArchivoNoPermitidoException extends RuntimeException {
}
