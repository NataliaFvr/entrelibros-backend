package com.uade.entrelibros.backend.exceptions;

public class ListaVaciaException extends RuntimeException {
    public ListaVaciaException(String mensaje) {
        super(mensaje);
    }
}