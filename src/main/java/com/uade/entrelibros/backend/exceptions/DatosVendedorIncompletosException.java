package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Para habilitar a un vendedor, el nombre de tienda, la provincia y el teléfono son obligatorios")
public class DatosVendedorIncompletosException extends EntreLibrosException {
}
