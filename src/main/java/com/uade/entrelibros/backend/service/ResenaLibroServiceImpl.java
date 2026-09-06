package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.ResenaLibro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
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

    public ResenaLibro crearResena(Usuario comprador, Long idOrdenItem, Integer calificacion, String comentario)
            throws OrdenItemNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException,
            AccionNoPermitidaException {

        if (calificacion == null || calificacion < 1 || calificacion > 5)
            throw new CalificacionInvalidaException();

        OrdenItem ordenItem = ordenItemRepository.findById(idOrdenItem)
                .orElseThrow(OrdenItemNoEncontradoException::new);

        // Ownership: el que reseña tiene que ser el comprador real de ese item de orden
        if (ordenItem.getOrden() == null
                || ordenItem.getOrden().getComprador() == null
                || !ordenItem.getOrden().getComprador().getId().equals(comprador.getId())) {
            throw new AccionNoPermitidaException();
        }

        // Un libro comprado se puede resenar una sola vez por item de la orden
        if (!resenaLibroRepository.findByOrdenItemId(idOrdenItem).isEmpty())
            throw new ResenaDuplicadaException();

        return resenaLibroRepository.save(new ResenaLibro(ordenItem, calificacion, comentario));
    }

    @Transactional
    public ResenaLibro modificarResena(Long idResena, Usuario comprador, Integer calificacion, String comentario) {
        ResenaLibro resena = getResenaById(idResena);
        validarAutor(resena, comprador);

        if (calificacion != null) {
            validarCalificacion(calificacion);
            resena.setCalificacion(calificacion);
        }
        if (comentario != null) {
            resena.setComentario(comentario);
        }

        return resenaLibroRepository.save(resena);
    }

    @Transactional
    public void eliminarResena(Long idResena, Usuario comprador) {
        ResenaLibro resena = getResenaById(idResena);
        validarAutor(resena, comprador);
        resenaLibroRepository.delete(resena);
    }

    private void validarCalificacion(Integer calificacion) {
        if (calificacion < 1 || calificacion > 5) {
            throw new CalificacionInvalidaException();
        }
    }

    private void validarAutor(ResenaLibro resena, Usuario comprador) {
        if (comprador == null || resena.getOrdenItem() == null || resena.getOrdenItem().getOrden() == null
                || resena.getOrdenItem().getOrden().getComprador() == null
                || !resena.getOrdenItem().getOrden().getComprador().getId().equals(comprador.getId())) {
            throw new AccionNoPermitidaException();
        }
    }
}
