package com.uade.entrelibros.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.dto.HistorialModeracionResponse;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.repository.HistorialModeracionRepository;
import com.uade.entrelibros.backend.repository.LibroRepository;

@Service
public class HistorialModeracionServiceImpl implements HistorialModeracionService {

    @Autowired
    private HistorialModeracionRepository historialModeracionRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public Page<HistorialModeracionResponse> getHistorialPorLibro(Long idLibro, Pageable pageable) {
        if (!libroRepository.existsById(idLibro)) {
            throw new LibroNoEncontradoException();
        }
        return historialModeracionRepository.findByLibroIdOrderByFechaDesc(idLibro, pageable)
                .map(HistorialModeracionResponse::new);
    }

    @Override
    public Page<HistorialModeracionResponse> getHistorialCompleto(Pageable pageable) {
        return historialModeracionRepository.findAllByOrderByFechaDesc(pageable)
                .map(HistorialModeracionResponse::new);
    }
}
