package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.EnvioItem;
import com.uade.entrelibros.backend.entity.ResenaVendedor;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CalificacionInvalidaException;
import com.uade.entrelibros.backend.exceptions.EnvioItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.ResenaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.ResenaVendedorNoEncontradaException;
import com.uade.entrelibros.backend.repository.EnvioItemRepository;
import com.uade.entrelibros.backend.repository.ResenaVendedorRepository;

@Service
public class ResenaVendedorServiceImpl implements ResenaVendedorService {

    @Autowired
    private ResenaVendedorRepository resenaVendedorRepository;
    @Autowired
    private EnvioItemRepository envioItemRepository;

    public List<ResenaVendedor> getResenas() {
        List<ResenaVendedor> resenas = resenaVendedorRepository.findAll();
        if (resenas.isEmpty()) {
            throw new ListaVaciaException("No hay reseñas de vendedores registradas");
        }
        return resenas;
    }


    public ResenaVendedor getResenaById(Long idResena) throws ResenaVendedorNoEncontradaException {
        return resenaVendedorRepository.findById(idResena)
                .orElseThrow(ResenaVendedorNoEncontradaException::new);
    }

    public List<ResenaVendedor> getResenasByVendedor(Long idVendedor) {
        List<ResenaVendedor> resenas = resenaVendedorRepository.findByVendedorId(idVendedor);
        if (resenas.isEmpty()) {
            throw new ListaVaciaException("Ese vendedor todavía no tiene reseñas");
        }
        return resenas;
    }

    public ResenaVendedor crearResena(Usuario comprador, Long idEnvioItem, Integer clasificacion, String comentario)
            throws EnvioItemNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException,
            AccionNoPermitidaException {

        if (clasificacion == null || clasificacion < 1 || clasificacion > 5)
            throw new CalificacionInvalidaException();

        EnvioItem envioItem = envioItemRepository.findById(idEnvioItem)
                .orElseThrow(EnvioItemNoEncontradoException::new);

        // Ownership: el que reseña tiene que ser el comprador real de esa orden/envio
        if (envioItem.getOrdenVendedor() == null
                || envioItem.getOrdenVendedor().getOrden() == null
                || envioItem.getOrdenVendedor().getOrden().getComprador() == null
                || !envioItem.getOrdenVendedor().getOrden().getComprador().getId().equals(comprador.getId())) {
            throw new AccionNoPermitidaException();
        }

        // El comprador resena una sola vez a ese vendedor por cada envio recibido
        if (!resenaVendedorRepository.findByEnvioItemIdAndCompradorId(idEnvioItem, comprador.getId()).isEmpty())
            throw new ResenaDuplicadaException();

        return resenaVendedorRepository.save(
                new ResenaVendedor(envioItem, comprador, clasificacion, comentario));
    }

    @Transactional
    public ResenaVendedor modificarResena(Long idResena, Usuario comprador, Integer clasificacion,
            String comentario) {
        ResenaVendedor resena = getResenaById(idResena);
        validarAutor(resena, comprador);

        if (clasificacion != null) {
            validarClasificacion(clasificacion);
            resena.setClasificacion(clasificacion);
        }
        if (comentario != null) {
            resena.setComentario(comentario);
        }

        return resenaVendedorRepository.save(resena);
    }

    @Transactional
    public void eliminarResena(Long idResena, Usuario comprador) {
        ResenaVendedor resena = getResenaById(idResena);
        validarAutor(resena, comprador);
        resenaVendedorRepository.delete(resena);
    }

    private void validarClasificacion(Integer clasificacion) {
        if (clasificacion < 1 || clasificacion > 5) {
            throw new CalificacionInvalidaException();
        }
    }

    private void validarAutor(ResenaVendedor resena, Usuario comprador) {
        if (comprador == null || resena.getComprador() == null
                || !resena.getComprador().getId().equals(comprador.getId())) {
            throw new AccionNoPermitidaException();
        }
    }
}
