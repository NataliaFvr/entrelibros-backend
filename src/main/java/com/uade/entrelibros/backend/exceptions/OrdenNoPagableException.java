package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "La orden no esta pendiente de pago (ya fue pagada, cancelada o la reserva vencio)")
public class OrdenNoPagableException extends RuntimeException {
}
