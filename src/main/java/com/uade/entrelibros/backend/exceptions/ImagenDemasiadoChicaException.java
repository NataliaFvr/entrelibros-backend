package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "La imagen es muy chica: el lado menor tiene que medir al menos 150 px")
public class ImagenDemasiadoChicaException extends EntreLibrosException {
}