package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "El usuario no tiene foto")
public class ImagenUsuarioNoEncontradaException extends EntreLibrosException {
}
