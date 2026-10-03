package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Esa solicitud ya fue resuelta")
public class SolicitudYaResueltaException extends EntreLibrosException {
}