package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.Direccion;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.DireccionRequest;

public interface DireccionService {

    List<Direccion> getDirecciones(Long idUsuario);

    Direccion crear(Usuario usuario, DireccionRequest r);

    void eliminar(Long idUsuario, Long idDireccion);

    List<Direccion> marcarPrincipal(Long idUsuario, Long idDireccion);
}
