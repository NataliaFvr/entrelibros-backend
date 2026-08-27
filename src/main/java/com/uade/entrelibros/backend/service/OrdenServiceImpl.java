package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.repository.OrdenRepository;

@Service
public class OrdenServiceImpl implements OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    public List<Orden> getOrdenes() {
        return ordenRepository.findAll();
    }

    public Orden getOrdenById(Long idOrden) throws OrdenNoEncontradaException {
        return ordenRepository.findById(idOrden)
                .orElseThrow(OrdenNoEncontradaException::new);
    }

    public List<Orden> getOrdenesByComprador(Long idComprador) {
        return ordenRepository.findByCompradorId(idComprador);
    }
}