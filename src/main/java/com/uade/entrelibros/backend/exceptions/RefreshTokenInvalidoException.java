package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Refresh token invalido o vencido")
public class RefreshTokenInvalidoException extends EntreLibrosException {
}
