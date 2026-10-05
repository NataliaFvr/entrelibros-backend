package com.uade.entrelibros.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.EstadoModeracion;
import com.uade.entrelibros.backend.entity.EstadoPublicacion;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Marcapagina;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.LibroNoDisponibleException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.MarcapaginaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.MarcapaginaNoEncontradaException;
import com.uade.entrelibros.backend.repository.LibroRepository;
import com.uade.entrelibros.backend.repository.MarcapaginaRepository;

@Service
public class MarcapaginaServiceImpl implements MarcapaginaService {

    @Autowired
    private MarcapaginaRepository marcapaginaRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public Page<Marcapagina> getMarcapaginas(Long idUsuario, Pageable pageable) {
        Page<Marcapagina> marcapaginas = marcapaginaRepository.findVisiblesByUsuarioId(idUsuario, pageable);
        if (marcapaginas.isEmpty()) {
            throw new ListaVaciaException("No tenes libros en tus marcapaginas");
        }
        return marcapaginas;
    }

    @Override
    @Transactional
    public Marcapagina agregar(Usuario usuario, Long idLibro) {
        if (idLibro == null) {
            throw new LibroNoEncontradoException();
        }

        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(LibroNoEncontradoException::new);


        if (libro.getEstadoPublicacion() != EstadoPublicacion.ACTIVA
                || libro.getEstadoModeracion() != EstadoModeracion.ACEPTADO) {
            throw new LibroNoDisponibleException();
        }

        if (marcapaginaRepository.existsByUsuarioIdAndLibroId(usuario.getId(), idLibro)) {
            throw new MarcapaginaDuplicadaException();
        }

        try {
            return marcapaginaRepository.saveAndFlush(new Marcapagina(usuario, libro));
        } catch (DataIntegrityViolationException e) {
            // Dos requests simultaneos: gana la constraint unica de la base
            throw new MarcapaginaDuplicadaException();
        }
    }


    @Override
    @Transactional
    public void quitar(Long idUsuario, Long idLibro) {
        Marcapagina marcapagina = marcapaginaRepository.findByUsuarioIdAndLibroId(idUsuario, idLibro)
                .orElseThrow(MarcapaginaNoEncontradaException::new);
        marcapaginaRepository.delete(marcapagina);
    }
}