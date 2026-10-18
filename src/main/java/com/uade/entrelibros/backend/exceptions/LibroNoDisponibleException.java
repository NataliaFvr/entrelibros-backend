package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Libro no disponible")
public class LibroNoDisponibleException extends EntreLibrosException {

    private final String mensaje;

    public LibroNoDisponibleException() {
        this.mensaje = null;
    }

    // Con el titulo, el comprador sabe cual sacar de la estanteria
    public LibroNoDisponibleException(String titulo) {
        this.mensaje = "\"" + titulo + "\" ya no está disponible. Sacalo de tu estantería para seguir con la compra.";
    }

    @Override
    public String getMessage() {
        return mensaje;
    }
}
