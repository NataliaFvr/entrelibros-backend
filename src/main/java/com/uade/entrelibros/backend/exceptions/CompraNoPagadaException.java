package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "La compra debe estar pagada para poder dejar una resena")
public class CompraNoPagadaException extends RuntimeException {
}
