package com.uade.entrelibros.backend.entity.dto;

import com.uade.entrelibros.backend.entity.ImagenLibro;
import lombok.Data;

@Data
public class ImagenLibroResponse {

    private Long id;
    private String tipoContenido;
    private Integer orden;
    private Long idLibro;

    public static ImagenLibroResponse from(ImagenLibro imagen) {
        ImagenLibroResponse r = new ImagenLibroResponse();
        r.id = imagen.getId();
        r.tipoContenido = imagen.getTipoContenido();
        r.orden = imagen.getOrden();
        r.idLibro = imagen.getLibro().getId();
        return r;
    }
}
