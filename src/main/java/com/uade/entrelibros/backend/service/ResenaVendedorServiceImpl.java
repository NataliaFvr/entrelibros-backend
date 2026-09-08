package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.EstadoPago;
import com.uade.entrelibros.backend.entity.Pago;
import com.uade.entrelibros.backend.entity.ResenaVendedor;
import com.uade.entrelibros.backend.entity.OrdenVendedor;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CalificacionInvalidaException;
import com.uade.entrelibros.backend.exceptions.CompraNoPagadaException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.ResenaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.ResenaVendedorNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;
import com.uade.entrelibros.backend.repository.PagoRepository;
import com.uade.entrelibros.backend.repository.OrdenVendedorRepository;
import com.uade.entrelibros.backend.repository.ResenaVendedorRepository;

@Service
public class ResenaVendedorServiceImpl implements ResenaVendedorService {

    @Autowired
    private ResenaVendedorRepository resenaVendedorRepository;
    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private OrdenVendedorRepository ordenVendedorRepository;

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

    public ResenaVendedor crearResena(Usuario comprador, Long idPago, Long idVendedor, Integer clasificacion,
            String comentario)
            throws PagoNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException,
            AccionNoPermitidaException {

        if (clasificacion == null || clasificacion < 1 || clasificacion > 5)
            throw new CalificacionInvalidaException();

        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(PagoNoEncontradoException::new);

        // El pago debe corresponder al comprador autenticado.
        if (pago.getOrden() == null
                || pago.getOrden().getComprador() == null
                || !pago.getOrden().getComprador().getId().equals(comprador.getId())) {
            throw new AccionNoPermitidaException();
        }

        if (pago.getResultado() != EstadoPago.SIMULADO_APROBADO) {
            throw new CompraNoPagadaException();
        }

        List<OrdenVendedor> ordenesVendedor = ordenVendedorRepository.findByOrdenId(pago.getOrden().getId());
        Usuario vendedor = ordenesVendedor.stream()
                .map(OrdenVendedor::getVendedor)
                .filter(v -> v != null && v.getId().equals(idVendedor))
                .findFirst()
                .orElseThrow(AccionNoPermitidaException::new);

        // El comprador puede reseñar una vez a cada vendedor incluido en el pago.
        if (!resenaVendedorRepository
                .findByPagoIdAndVendedorIdAndCompradorId(idPago, vendedor.getId(), comprador.getId()).isEmpty())
            throw new ResenaDuplicadaException();

        return resenaVendedorRepository.save(
                new ResenaVendedor(pago, vendedor, comprador, clasificacion, comentario));
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
