package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.Envio;
import com.uade.entrelibros.backend.entity.ZonaEnvio;
import com.uade.entrelibros.backend.exceptions.EnvioDuplicadoException;
import com.uade.entrelibros.backend.exceptions.EnvioNoEncontradoException;

public interface EnvioService {

    List<Envio> getEnvios();

    Envio getEnvioById(Long idEnvio) throws EnvioNoEncontradoException;

    Envio crearEnvio(ZonaEnvio zona, Double costoFijo) throws EnvioDuplicadoException;
}
