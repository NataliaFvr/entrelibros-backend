package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "La notificacion no tiene destinatario, tipo o mensaje")
public class NotificacionInvalidaException extends EntreLibrosException {
}
