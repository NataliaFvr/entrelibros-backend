package com.uade.entrelibros.backend.entity.dto;

import java.time.LocalDateTime;

import com.uade.entrelibros.backend.entity.Marcapagina;
import lombok.Data;

@Data
public class MarcapaginaResponse {

    private Long id;
    private LocalDateTime fechaGuardado;
    private LibroResponse libro;

    public static MarcapaginaResponse from(Marcapagina marcapagina) {
        MarcapaginaResponse r = new MarcapaginaResponse();
        r.id = marcapagina.getId();
        r.fechaGuardado = marcapagina.getFechaGuardado();
        if (marcapagina.getLibro() != null) {
            r.libro = LibroResponse.from(marcapagina.getLibro());
        }
        return r;
    }
}