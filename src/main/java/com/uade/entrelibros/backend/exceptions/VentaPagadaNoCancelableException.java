package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "No podes cancelar una venta que ya fue pagada")
public class VentaPagadaNoCancelableException extends EntreLibrosException {
}
