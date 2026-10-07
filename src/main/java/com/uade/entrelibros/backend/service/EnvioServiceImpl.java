package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Envio;
import com.uade.entrelibros.backend.entity.ZonaEnvio;
import com.uade.entrelibros.backend.exceptions.EnvioDuplicadoException;
import com.uade.entrelibros.backend.exceptions.EnvioNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.repository.EnvioRepository;
import jakarta.annotation.PostConstruct;

@Service
public class EnvioServiceImpl implements EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    @PostConstruct
    public void inicializarTarifas() {
        asegurarTarifas();
    }

    private void asegurarTarifas() {
        if (envioRepository.findByZona(ZonaEnvio.MISMA_PROVINCIA) == null) {
            envioRepository.save(new Envio(ZonaEnvio.MISMA_PROVINCIA, 1800.0));
        }
        if (envioRepository.findByZona(ZonaEnvio.DISTINTA_PROVINCIA) == null) {
            envioRepository.save(new Envio(ZonaEnvio.DISTINTA_PROVINCIA, 3500.0));
        }
    }

    public List<Envio> getEnvios() {
        asegurarTarifas();
        List<Envio> envios = envioRepository.findAll();
        if (envios.isEmpty()) {
            throw new ListaVaciaException("No hay tarifas de envío registradas");
        }
        return envios;
    }

    public Envio getEnvioById(Long idEnvio) {
        return envioRepository.findById(idEnvio)
                .orElseThrow(EnvioNoEncontradoException::new);
    }

    public Envio crearEnvio(ZonaEnvio zona, Double costoFijo) {
        if (zona == null || costoFijo == null || costoFijo <= 0) {
            throw new IllegalArgumentException("La zona y el costo fijo deben ser válidos");
        }
        Envio existente = envioRepository.findByZona(zona);
        if (existente != null)
            throw new EnvioDuplicadoException();
        return envioRepository.save(new Envio(zona, costoFijo));
    }

    public Double getCostoPorZona(ZonaEnvio zona) {
        Envio envio = envioRepository.findByZona(zona);
        if (envio == null) {
            throw new EnvioNoEncontradoException();
        }
        return envio.getCostoFijo();
    }

    @Override
    public Envio actualizarCosto(Long idEnvio, Double costoFijo) {
        if (costoFijo == null || costoFijo <= 0) {
            throw new IllegalArgumentException("El costo fijo debe ser mayor a cero");
        }
        Envio envio = envioRepository.findById(idEnvio)
                .orElseThrow(EnvioNoEncontradoException::new);
        envio.setCostoFijo(costoFijo);
        return envioRepository.save(envio);
    }
}