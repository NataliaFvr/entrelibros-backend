package com.uade.entrelibros.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.entrelibros.backend.entity.dto.HistorialModeracionResponse;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;

public interface HistorialModeracionService {

    Page<HistorialModeracionResponse> getHistorialPorLibro(Long idLibro, Pageable pageable)
            throws LibroNoEncontradoException;

    Page<HistorialModeracionResponse> getHistorialCompleto(Pageable pageable);
}