package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Estado de filtro invalido. estadoModeracion: EN_REVISION | ACEPTADO | RECHAZADO; estadoPublicacion: ACTIVA | DADA_DE_BAJA")
public class EstadoFiltroInvalidoException extends EntreLibrosException {
}