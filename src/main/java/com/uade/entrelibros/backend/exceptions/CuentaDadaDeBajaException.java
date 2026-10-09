package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "La cuenta está dada de baja")
public class CuentaDadaDeBajaException extends EntreLibrosException {
}
