package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Ese libro ya esta en tus marcapaginas")
public class MarcapaginaDuplicadaException extends EntreLibrosException {
}