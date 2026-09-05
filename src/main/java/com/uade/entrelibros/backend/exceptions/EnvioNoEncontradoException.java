package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No existe una tarifa de envio para esa zona")
public class EnvioNoEncontradoException extends RuntimeException {
}
