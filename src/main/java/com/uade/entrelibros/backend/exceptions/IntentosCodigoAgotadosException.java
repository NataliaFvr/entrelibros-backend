package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS, reason = "Superaste los intentos permitidos. Pedí un código nuevo.")
public class IntentosCodigoAgotadosException extends EntreLibrosException {
}