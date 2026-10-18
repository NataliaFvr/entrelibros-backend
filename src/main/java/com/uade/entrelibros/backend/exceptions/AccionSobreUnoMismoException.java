package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Un admin no puede darse de baja ni cambiarse el rol: si es el unico, nadie mas podria administrar
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No podés darte de baja ni cambiar tu propio rol")
public class AccionSobreUnoMismoException extends EntreLibrosException {
}
