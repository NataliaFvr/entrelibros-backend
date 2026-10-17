package com.uade.entrelibros.backend.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Como CampoInvalidoException, pero con varios campos a la vez (ej.: todos los datos de la tarjeta).
// Responde 400 con { "<campo>": "<mensaje>", ... }: el front muestra cada error debajo de su input.
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CamposInvalidosException extends EntreLibrosException {

    private final Map<String, String> errores;

    public CamposInvalidosException(Map<String, String> errores) {
        this.errores = errores;
    }

    public Map<String, String> getErrores() {
        return errores;
    }

    @Override
    public String getMessage() {
        return String.join(" ", errores.values());
    }
}
