package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "La resolucion debe ser APROBADA o RECHAZADA")
public class ResolucionInvalidaException extends EntreLibrosException {
}