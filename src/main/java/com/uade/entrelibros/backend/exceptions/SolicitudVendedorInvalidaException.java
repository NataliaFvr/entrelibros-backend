package com.uade.entrelibros.backend.exceptions;
 
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
 
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No se puede procesar la solicitud de vendedor en su estado actual")
public class SolicitudVendedorInvalidaException extends EntreLibrosException {
}
 