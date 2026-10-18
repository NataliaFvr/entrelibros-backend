package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No hay stock de este libro")
public class StockInsuficienteException extends EntreLibrosException {

    private final String mensaje;

    public StockInsuficienteException() {
        this.mensaje = null;
    }

    public StockInsuficienteException(String titulo) {
        this.mensaje = "No hay stock suficiente de \"" + titulo + "\". Bajá la cantidad o sacalo de tu estantería.";
    }

    @Override
    public String getMessage() {
        return mensaje;
    }
}
