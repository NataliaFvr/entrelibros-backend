package com.uade.entrelibros.backend.entity.dto;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.uade.entrelibros.backend.entity.Libro;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

// GET /libros/mios: todo LibroResponse (incluidos los snapshot* de la moderacion y fechaSolicitudRevision)
// + motivoRechazo. Se copian TODAS las propiedades de LibroResponse con BeanUtils para que un campo nuevo
// en LibroResponse no se pierda aca (antes se copiaban a mano y faltaban los snapshot*).
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LibroMioResponse extends LibroResponse {

    // Comentario del ultimo rechazo. null si el libro NO esta RECHAZADO hoy.
    private String motivoRechazo;

    public static LibroMioResponse from(Libro libro, List<String> categorias, String motivoRechazo) {
        LibroMioResponse response = new LibroMioResponse();
        BeanUtils.copyProperties(LibroResponse.from(libro, categorias), response);
        response.setMotivoRechazo(motivoRechazo);
        return response;
    }
}