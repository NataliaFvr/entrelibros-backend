package com.uade.entrelibros.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.entrelibros.backend.entity.Marcapagina;
import com.uade.entrelibros.backend.entity.Usuario;

public interface MarcapaginaService {

    Page<Marcapagina> getMarcapaginas(Long idUsuario, Pageable pageable);

    Marcapagina agregar(Usuario usuario, Long idLibro);

    void quitar(Long idUsuario, Long idLibro);
}