package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.OrdenVendedor;
import com.uade.entrelibros.backend.entity.EstadoOrdenVendedor;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.OrdenVendedorNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.repository.OrdenRepository;
import com.uade.entrelibros.backend.repository.OrdenVendedorRepository;

@Service
public class OrdenServiceImpl implements OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;
    @Autowired
    private OrdenVendedorRepository ordenVendedorRepository;

    public List<Orden> getOrdenes(Usuario usuario) throws AccionNoPermitidaException {
        if (usuario.getRol() != Rol.ADMIN) {
            throw new AccionNoPermitidaException();
        }
        return ordenRepository.findAll();
    }

    public Orden getOrdenById(Long idOrden, Usuario usuario)
            throws OrdenNoEncontradaException, AccionNoPermitidaException {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(OrdenNoEncontradaException::new);

        boolean esComprador = orden.getComprador().getId().equals(usuario.getId());
        boolean esVendedorDeLaOrden = ordenVendedorRepository.findByOrdenIdAndVendedorId(idOrden, usuario.getId())
                .isPresent();

        if (usuario.getRol() != Rol.ADMIN && !esComprador && !esVendedorDeLaOrden) {
            throw new AccionNoPermitidaException();
        }

        return orden;
    }

    public List<Orden> getOrdenesByComprador(Usuario comprador) {
        return ordenRepository.findByCompradorId(comprador.getId());
    }

    public List<OrdenVendedor> getOrdenesDelVendedor(Usuario vendedor) throws RolInvalidoException {
        validarVendedor(vendedor);
        return ordenVendedorRepository.findByVendedorId(vendedor.getId());
    }

    public OrdenVendedor cancelarOrdenVendedor(Long idOrdenVendedor, Usuario vendedor)
            throws OrdenVendedorNoEncontradaException, RolInvalidoException, AccionNoPermitidaException {
        validarVendedor(vendedor);
        OrdenVendedor ordenVendedor = ordenVendedorRepository.findById(idOrdenVendedor)
                .orElseThrow(OrdenVendedorNoEncontradaException::new);

        if (!ordenVendedor.getVendedor().getId().equals(vendedor.getId())) {
            throw new AccionNoPermitidaException();
        }

        ordenVendedor.setEstado(EstadoOrdenVendedor.CANCELADA);
        return ordenVendedorRepository.save(ordenVendedor);
    }

    private void validarVendedor(Usuario vendedor) throws RolInvalidoException {
        if (vendedor.getRol() != Rol.VENDEDOR) {
            throw new RolInvalidoException();
        }
    }
}
