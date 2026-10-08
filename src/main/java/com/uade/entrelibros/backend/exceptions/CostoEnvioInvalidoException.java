package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El costo del envío debe ser mayor a cero")
public class CostoEnvioInvalidoException extends EntreLibrosException {
}
