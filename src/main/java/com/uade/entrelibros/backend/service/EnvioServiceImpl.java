package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Envio;
import com.uade.entrelibros.backend.entity.ZonaEnvio;
import com.uade.entrelibros.backend.exceptions.EnvioDuplicadoException;
import com.uade.entrelibros.backend.exceptions.EnvioNoEncontradoException;
import com.uade.entrelibros.backend.repository.EnvioRepository;

@Service
public class EnvioServiceImpl implements EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    public List<Envio> getEnvios() {
        return envioRepository.findAll();
    }

    public Envio getEnvioById(Long idEnvio) {
        return envioRepository.findById(idEnvio)
                .orElseThrow(EnvioNoEncontradoException::new);
    }

    public Envio crearEnvio(ZonaEnvio zona, Double costoFijo) {
        Envio existente = envioRepository.findByZona(zona);
        if (existente != null)
            throw new EnvioDuplicadoException();
        return envioRepository.save(new Envio(zona, costoFijo));
    }
}
