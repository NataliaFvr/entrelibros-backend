package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.EnvioItem;
import com.uade.entrelibros.backend.entity.ResenaVendedor;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.CalificacionInvalidaException;
import com.uade.entrelibros.backend.exceptions.EnvioItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ResenaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.ResenaVendedorNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.UsuarioNoEncontradoException;
import com.uade.entrelibros.backend.repository.EnvioItemRepository;
import com.uade.entrelibros.backend.repository.ResenaVendedorRepository;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

@Service
public class ResenaVendedorServiceImpl implements ResenaVendedorService {

    @Autowired
    private ResenaVendedorRepository resenaVendedorRepository;
    @Autowired
    private EnvioItemRepository envioItemRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<ResenaVendedor> getResenas() {
        return resenaVendedorRepository.findAll();
    }

    public ResenaVendedor getResenaById(Long idResena) throws ResenaVendedorNoEncontradaException {
        return resenaVendedorRepository.findById(idResena)
                .orElseThrow(ResenaVendedorNoEncontradaException::new);
    }

    public List<ResenaVendedor> getResenasByVendedor(Long idVendedor) {
        return resenaVendedorRepository.findByVendedorId(idVendedor);
    }

    public ResenaVendedor crearResena(Long idEnvioItem, Long idComprador, Integer clasificacion, String comentario)
            throws EnvioItemNoEncontradoException, UsuarioNoEncontradoException,
            CalificacionInvalidaException, ResenaDuplicadaException {

        if (clasificacion == null || clasificacion < 1 || clasificacion > 5)
            throw new CalificacionInvalidaException();

        EnvioItem envioItem = envioItemRepository.findById(idEnvioItem)
                .orElseThrow(EnvioItemNoEncontradoException::new);

        Usuario comprador = usuarioRepository.findById(idComprador)
                .orElseThrow(UsuarioNoEncontradoException::new);

        // El comprador resena una sola vez a ese vendedor por cada envio recibido
        if (!resenaVendedorRepository.findByEnvioItemIdAndCompradorId(idEnvioItem, idComprador).isEmpty())
            throw new ResenaDuplicadaException();

        return resenaVendedorRepository.save(
                new ResenaVendedor(envioItem, comprador, clasificacion, comentario));
    }
}
