package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "La zona de envio debe ser misma o distinta")
public class ZonaEnvioInvalidaException extends EntreLibrosException {
}