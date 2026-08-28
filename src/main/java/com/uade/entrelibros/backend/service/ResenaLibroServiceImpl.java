package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.ResenaLibro;
import com.uade.entrelibros.backend.exceptions.CalificacionInvalidaException;
import com.uade.entrelibros.backend.exceptions.OrdenItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ResenaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.ResenaLibroNoEncontradaException;
import com.uade.entrelibros.backend.repository.OrdenItemRepository;
import com.uade.entrelibros.backend.repository.ResenaLibroRepository;

@Service
public class ResenaLibroServiceImpl implements ResenaLibroService {

    @Autowired
    private ResenaLibroRepository resenaLibroRepository;
    @Autowired
    private OrdenItemRepository ordenItemRepository;

    public List<ResenaLibro> getResenas() {
        return resenaLibroRepository.findAll();
    }

    public ResenaLibro getResenaById(Long idResena) throws ResenaLibroNoEncontradaException {
        return resenaLibroRepository.findById(idResena)
                .orElseThrow(ResenaLibroNoEncontradaException::new);
    }

    public List<ResenaLibro> getResenasByLibro(Long idLibro) {
        return resenaLibroRepository.findByLibroId(idLibro);
    }

    public ResenaLibro crearResena(Long idOrdenItem, Integer calificacion, String comentario)
            throws OrdenItemNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException {

        if (calificacion == null || calificacion < 1 || calificacion > 5)
            throw new CalificacionInvalidaException();

        OrdenItem ordenItem = ordenItemRepository.findById(idOrdenItem)
                .orElseThrow(OrdenItemNoEncontradoException::new);

        // Un libro comprado se puede resenar una sola vez por item de la orden
        if (!resenaLibroRepository.findByOrdenItemId(idOrdenItem).isEmpty())
            throw new ResenaDuplicadaException();

        return resenaLibroRepository.save(new ResenaLibro(ordenItem, calificacion, comentario));
    }
}
