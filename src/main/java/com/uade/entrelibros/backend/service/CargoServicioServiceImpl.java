package com.uade.entrelibros.backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.ConfiguracionCargo;
import com.uade.entrelibros.backend.repository.ConfiguracionCargoRepository;

@Service
public class CargoServicioServiceImpl implements CargoServicioService {

    @Autowired
    private ConfiguracionCargoRepository configuracionCargoRepository;

    @Override
    public ConfiguracionCargo obtener() {
        return configuracionCargoRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> new ConfiguracionCargo(0.0));
    }

    @Override
    @Transactional
    public ConfiguracionCargo actualizar(Double porcentaje) {
        ConfiguracionCargo config = configuracionCargoRepository.findFirstByOrderByIdAsc()
                .orElseGet(ConfiguracionCargo::new);
        config.setPorcentaje(porcentaje);
        config.setFechaActualizacion(LocalDateTime.now());
        return configuracionCargoRepository.save(config);
    }

    @Override
    public double porcentajeVigente() {
        Double porcentaje = obtener().getPorcentaje();
        return porcentaje != null ? porcentaje : 0.0;
    }
}