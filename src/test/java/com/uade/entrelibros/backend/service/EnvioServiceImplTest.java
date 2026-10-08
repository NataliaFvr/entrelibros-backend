package com.uade.entrelibros.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import com.uade.entrelibros.backend.entity.Envio;
import com.uade.entrelibros.backend.entity.ZonaEnvio;
import com.uade.entrelibros.backend.exceptions.CostoEnvioInvalidoException;
import com.uade.entrelibros.backend.exceptions.ZonaEnvioInvalidaException;
import com.uade.entrelibros.backend.repository.EnvioRepository;

@ExtendWith(MockitoExtension.class)
class EnvioServiceImplTest {

    @Mock
    private EnvioRepository envioRepository;
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private EnvioServiceImpl envioService;

    @Test
    void actualizaElCostoDeUnaTarifa() {
        Envio tarifa = new Envio(ZonaEnvio.MISMA_PROVINCIA, 1800.0);
        when(envioRepository.findById(1L)).thenReturn(Optional.of(tarifa));
        when(envioRepository.save(tarifa)).thenReturn(tarifa);

        assertEquals(2000.0, envioService.actualizarCosto(1L, 2000.0).getCostoFijo());
    }

    @ParameterizedTest
    @ValueSource(doubles = { 0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY })
    void elCostoDebeSerMayorACero(double costo) {
        assertThrows(CostoEnvioInvalidoException.class, () -> envioService.actualizarCosto(1L, costo));
        assertThrows(CostoEnvioInvalidoException.class,
                () -> envioService.crearEnvio(ZonaEnvio.MISMA_PROVINCIA, costo));
        verify(envioRepository, never()).save(any());
    }

    @Test
    void zonaInvalidaDevuelveZonaEnvioInvalida() {
        assertThrows(ZonaEnvioInvalidaException.class, () -> envioService.crearEnvio(null, 1800.0));
    }
}