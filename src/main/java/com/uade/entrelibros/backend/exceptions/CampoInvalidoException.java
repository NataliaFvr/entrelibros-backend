package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Error de validacion de UN campo, para reglas que no se pueden escribir como anotaciones del DTO
// (dependen de otros datos, de la fecha o de la base). Responde 400 con { "<campo>": "<mensaje>" }:
// el mismo formato que los errores de @Valid, asi el front lo muestra debajo del input sin validar nada por su cuenta.
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CampoInvalidoException extends EntreLibrosException {

    private final String campo;
    private final String mensaje;

    public CampoInvalidoException(String campo, String mensaje) {
        this.campo = campo;
        this.mensaje = mensaje;
    }

    public String getCampo() {
        return campo;
    }

    public String getMensaje() {
        return mensaje;
    }

    @Override
    public String getMessage() {
        return mensaje;
    }
}