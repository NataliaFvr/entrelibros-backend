package com.uade.entrelibros.backend.entity.dto;

import java.time.LocalDate;

import com.uade.entrelibros.backend.entity.Usuario;

import lombok.Data;

@Data
public class VendedorPerfilResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String nombreTienda;
    private String provincia;
    private String descripcion;
    private LocalDate desde;
    private boolean tieneFoto;
    private String avatar;
    private Double promedioResenas;
    private long cantidadResenas;

    public static VendedorPerfilResponse from(Usuario vendedor, Double promedioResenas, long cantidadResenas) {
        VendedorPerfilResponse response = new VendedorPerfilResponse();
        response.id = vendedor.getId();
        response.nombre = vendedor.getNombre();
        response.apellido = vendedor.getApellido();
        response.nombreTienda = vendedor.getNombreTienda();
        response.provincia = vendedor.getProvincia();
        response.descripcion = vendedor.getDescripcion();
        response.desde = vendedor.getFechaRegistro();
        response.tieneFoto = Boolean.TRUE.equals(vendedor.getTieneFoto());
        response.avatar = vendedor.getAvatar();
        response.promedioResenas = promedioResenas != null ? promedioResenas : 0.0;
        response.cantidadResenas = cantidadResenas;
        return response;
    }
}
