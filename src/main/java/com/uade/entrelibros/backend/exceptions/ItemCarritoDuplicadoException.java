package com.uade.entrelibros.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Dos pedidos simultáneos intentaron agregar el mismo libro al carrito: la restricción única
// (id_carrito, id_libro) rechaza el segundo. Mismo criterio que MarcapaginaDuplicadaException.
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Ese libro ya está en tu estantería. Actualizá la página para ver la cantidad.")
public class ItemCarritoDuplicadoException extends EntreLibrosException {
}