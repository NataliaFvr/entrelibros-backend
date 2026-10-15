package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No se puede quitar la única foto de un libro aprobado: subí la nueva antes de borrar la anterior")
public class UltimaFotoException extends EntreLibrosException {
}