package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El archivo supera el tamaño máximo permitido (10MB)")
public class ArchivoDemasiadoGrandeException extends RuntimeException {
}
