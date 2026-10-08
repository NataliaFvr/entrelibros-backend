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
import com.uade.entrelibros.backend.exceptions.ZonaEnvioInvalidaException;
import com.uade.entrelibros.backend.repository.EnvioRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EnvioServiceImpl implements EnvioService {

    private static final Logger log = LoggerFactory.getLogger(EnvioServiceImpl.class);

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
    // que ddl-auto=update no modifica: se pasa a texto. Los envio_item historicos NO se borran: se reasignan a la
    // tarifa nueva que corresponde (RESTO_PAIS -> DISTINTA; CABA / PROVINCIA_BA -> MISMA si la provincia del
    // vendedor coincide con la provincia destino de la orden, si no DISTINTA). Su "costo" (lo cobrado) no se toca.
    // Despues se borran las tarifas viejas. Es idempotente: si ya no hay zonas viejas no hace nada.
    private void migrarZonasViejas() {
        try {
            jdbcTemplate.execute("ALTER TABLE envio MODIFY zona VARCHAR(30)");
        } catch (Exception e) {
            return; // base nueva: la tabla todavia no existe o ya esta bien
        }
        try {
            Integer viejas = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM envio WHERE zona NOT IN ('MISMA_PROVINCIA', 'DISTINTA_PROVINCIA')",
                    Integer.class);
            if (viejas == null || viejas == 0) {
                return;
            }
            asegurarTarifas();
            jdbcTemplate.update(REASIGNAR_ENVIO_ITEMS);
            jdbcTemplate.update("DELETE FROM envio WHERE zona NOT IN ('MISMA_PROVINCIA', 'DISTINTA_PROVINCIA')");
        } catch (Exception e) {
            log.error("No se pudieron migrar las tarifas de envio viejas", e);
        }
    }

    private static final String PROVINCIA_SQL =
            "CONVERT(CASE WHEN UPPER(TRIM(%1$s)) IN ('CABA', 'CAPITAL FEDERAL') "
            + "THEN 'Ciudad Autonoma de Buenos Aires' ELSE TRIM(%1$s) END USING utf8mb4) COLLATE utf8mb4_0900_ai_ci";

    private static final String REASIGNAR_ENVIO_ITEMS =
            "UPDATE envio_item ei "
            + "JOIN envio viejo ON viejo.id = ei.id_envio "
            + "JOIN orden_vendedor ov ON ov.id = ei.id_orden_vendedor "
            + "JOIN orden o ON o.id = ov.id_orden "
            + "JOIN usuario v ON v.id = ov.id_vendedor "
            + "JOIN envio nuevo ON nuevo.zona = CASE "
            + "  WHEN viejo.zona = 'RESTO_PAIS' THEN 'DISTINTA_PROVINCIA' "
            + "  WHEN NULLIF(TRIM(v.provincia), '') IS NOT NULL AND NULLIF(TRIM(o.provincia_destino), '') IS NOT NULL "
            + "   AND " + String.format(PROVINCIA_SQL, "v.provincia") + " = " + String.format(PROVINCIA_SQL, "o.provincia_destino")
            + "  THEN 'MISMA_PROVINCIA' ELSE 'DISTINTA_PROVINCIA' END "
            + "SET ei.id_envio = nuevo.id "
            + "WHERE viejo.zona NOT IN ('MISMA_PROVINCIA', 'DISTINTA_PROVINCIA')";

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
            throw new ZonaEnvioInvalidaException();
        }
        validarCosto(costoFijo);
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
        validarCosto(costoFijo);
        Envio envio = envioRepository.findById(idEnvio)
                .orElseThrow(EnvioNoEncontradoException::new);
        envio.setCostoFijo(costoFijo);
        return envioRepository.save(envio);
    }

    private void validarCosto(Double costoFijo) {
        if (costoFijo == null || costoFijo.isNaN() || costoFijo.isInfinite() || costoFijo <= 0) {
            throw new CostoEnvioInvalidoException();
        }
    }
}