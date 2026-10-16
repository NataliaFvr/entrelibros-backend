package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 400 y no 401: el front toma un 401 como sesion vencida y cerraria la sesion
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Para cambiar la contraseña ingresá tu contraseña actual correcta")
public class ContrasenaActualIncorrectaException extends EntreLibrosException {
}
