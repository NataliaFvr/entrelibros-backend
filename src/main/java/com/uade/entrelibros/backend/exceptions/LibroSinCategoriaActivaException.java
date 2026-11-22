package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El libro no tiene ninguna categoría disponible: editalo y elegí otra para poder aprobarlo o reactivarlo")
public class LibroSinCategoriaActivaException extends EntreLibrosException {
}