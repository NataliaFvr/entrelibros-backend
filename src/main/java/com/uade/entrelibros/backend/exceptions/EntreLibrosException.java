package com.uade.entrelibros.backend.exceptions;

// Clase padre de todas las excepciones propias de la aplicacion.
// Cada hija define su codigo HTTP y su mensaje con @ResponseStatus, y el
// GlobalExceptionHandler las convierte siempre al mismo formato: {"error": "..."}
public abstract class EntreLibrosException extends RuntimeException {
}
