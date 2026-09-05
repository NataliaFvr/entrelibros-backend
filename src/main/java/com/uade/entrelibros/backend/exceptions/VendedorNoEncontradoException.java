package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No existe un usuario vendedor con ese id")
public class VendedorNoEncontradoException extends RuntimeException {
}
