package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El carrito está vacío, no se puede confirmar la compra")
public class CarritoVacioException extends RuntimeException {
}
