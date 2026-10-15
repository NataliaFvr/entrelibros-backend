package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El libro tiene más de 5 fotos: el vendedor tiene que quitar las sobrantes antes de aprobarlo")
public class LibroConDemasiadasFotosException extends EntreLibrosException {
}