package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Envio;
import com.uade.entrelibros.backend.entity.ZonaEnvio;
import com.uade.entrelibros.backend.exceptions.CostoEnvioInvalidoException;
import com.uade.entrelibros.backend.exceptions.EnvioDuplicadoException;
import com.uade.entrelibros.backend.exceptions.EnvioNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.repository.EnvioRepository;
import jakarta.annotation.PostConstruct;

@Service
public class EnvioServiceImpl implements EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void inicializarTarifas() {
        migrarZonasViejas();
        asegurarTarifas();
    }

    // Las bases creadas con las zonas CABA / PROVINCIA_BA / RESTO_PAIS tienen la columna como enum de MySQL,
    // que ddl-auto=update no modifica: se pasa a texto y se borran las tarifas viejas para que el back arranque.
    private void migrarZonasViejas() {
        try {
            jdbcTemplate.execute("ALTER TABLE envio MODIFY zona VARCHAR(30)");
            jdbcTemplate.update("DELETE FROM envio_item WHERE id_envio IN "
                    + "(SELECT id FROM envio WHERE zona NOT IN ('MISMA_PROVINCIA', 'DISTINTA_PROVINCIA'))");
            jdbcTemplate.update("DELETE FROM envio WHERE zona NOT IN ('MISMA_PROVINCIA', 'DISTINTA_PROVINCIA')");
        } catch (Exception e) {
            // base nueva: las tablas ya se crean con las zonas actuales
        }
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
        if (zona == null) {
            throw new EnvioNoEncontradoException();
        }
        if (costoFijo == null || costoFijo <= 0) {
            throw new CostoEnvioInvalidoException();
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
            throw new CostoEnvioInvalidoException();
        }
        Envio envio = envioRepository.findById(idEnvio)
                .orElseThrow(EnvioNoEncontradoException::new);
        envio.setCostoFijo(costoFijo);
        return envioRepository.save(envio);
    }
}