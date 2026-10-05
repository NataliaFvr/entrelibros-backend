package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Ya existe una solicitud de vendedor pendiente para este usuario")
public class SolicitudPendienteExistenteException extends EntreLibrosException {
}