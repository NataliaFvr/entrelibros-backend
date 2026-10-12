package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El estado del libro debe ser NUEVO o USADO")
public class EstadoLibroInvalidoException extends EntreLibrosException {
}
