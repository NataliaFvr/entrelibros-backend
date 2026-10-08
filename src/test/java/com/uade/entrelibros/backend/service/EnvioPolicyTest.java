package com.uade.entrelibros.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uade.entrelibros.backend.entity.CarritoItem;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.ZonaEnvio;
import com.uade.entrelibros.backend.exceptions.EnvioNoEncontradoException;

@ExtendWith(MockitoExtension.class)
class EnvioPolicyTest {

    @Mock
    private EnvioService envioService;

    private EnvioPolicy envioPolicy;

    @BeforeEach
    void setUp() {
        envioPolicy = new EnvioPolicy(envioService);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "'  Córdoba  '|CORDOBA",
            "'entre   ríos '|ENTRE RIOS",
            "NEUQUÉN|NEUQUEN",
            "caba|CIUDAD AUTONOMA DE BUENOS AIRES",
            "Ciudad Autónoma de Buenos Aires|CIUDAD AUTONOMA DE BUENOS AIRES",
            "Buenos Aires|BUENOS AIRES"
    })
    void normalizaSinMayusculasTildesNiEspaciosDeMas(String entrada, String esperado) {
        assertEquals(esperado, EnvioPolicy.normalizar(entrada));
        assertEquals(esperado, EnvioPolicy.normalizarONull(entrada));
    }

    @Test
    void sinProvincia() {
        assertEquals("", EnvioPolicy.normalizar(null));
        assertNull(EnvioPolicy.normalizarONull("   "));
    }

    @Test
    void tipoDeEnvio() {
        assertEquals(ZonaEnvio.MISMA_PROVINCIA, EnvioPolicy.determinarTipo("Córdoba", " cordoba "));
        // el front manda "CABA" en el checkout y el perfil guarda el nombre largo
        assertEquals(ZonaEnvio.MISMA_PROVINCIA, EnvioPolicy.determinarTipo("Ciudad Autónoma de Buenos Aires", "CABA"));
        assertEquals(ZonaEnvio.DISTINTA_PROVINCIA, EnvioPolicy.determinarTipo("Buenos Aires", "CABA"));
        assertEquals(ZonaEnvio.DISTINTA_PROVINCIA, EnvioPolicy.determinarTipo(null, "Córdoba"));
        assertEquals(ZonaEnvio.DISTINTA_PROVINCIA, EnvioPolicy.determinarTipo("Córdoba", null));
        assertEquals(ZonaEnvio.DISTINTA_PROVINCIA, EnvioPolicy.determinarTipo("  ", "  "));
    }

    @Test
    void unEnvioPorVendedorAunqueCompreVariosLibrosSuyos() {
        when(envioService.getCostoPorZona(ZonaEnvio.MISMA_PROVINCIA)).thenReturn(1800.0);
        when(envioService.getCostoPorZona(ZonaEnvio.DISTINTA_PROVINCIA)).thenReturn(3500.0);
        Usuario cordobes = vendedor(1L, "Córdoba");
        Usuario salteno = vendedor(2L, "Salta");
        List<CarritoItem> items = List.of(
                item(libro(10L, cordobes), 2),
                item(libro(11L, cordobes), 1),
                item(libro(12L, salteno), 1));

        assertEquals(5300.0, envioPolicy.costoPorVendedor(items, "cordoba"));

        Map<Long, EnvioPolicy.EnvioVendedor> envios = envioPolicy.enviosPorVendedor(
                items.stream().map(CarritoItem::getLibro).toList(), "cordoba");
        assertEquals(ZonaEnvio.MISMA_PROVINCIA, envios.get(1L).zona());
        assertEquals(ZonaEnvio.DISTINTA_PROVINCIA, envios.get(2L).zona());
    }

    @Test
    void laTarifaSeConsultaUnaVezPorZona() {
        when(envioService.getCostoPorZona(ZonaEnvio.DISTINTA_PROVINCIA)).thenReturn(3500.0);
        List<CarritoItem> items = List.of(
                item(libro(10L, vendedor(1L, "Salta")), 1),
                item(libro(11L, vendedor(2L, "Jujuy")), 1),
                item(libro(12L, vendedor(3L, null)), 1));

        assertEquals(10500.0, envioPolicy.costoPorVendedor(items, "Córdoba"));
        verify(envioService, times(1)).getCostoPorZona(ZonaEnvio.DISTINTA_PROVINCIA);
    }

    @Test
    void sinTarifaPropagaEnvioNoEncontrado() {
        when(envioService.getCostoPorZona(ZonaEnvio.DISTINTA_PROVINCIA)).thenThrow(new EnvioNoEncontradoException());
        List<CarritoItem> items = List.of(item(libro(10L, vendedor(1L, "Salta")), 1));

        assertThrows(EnvioNoEncontradoException.class, () -> envioPolicy.costoPorVendedor(items, "Córdoba"));
    }

    @Test
    void zonaEnvioDesdeJson() {
        assertEquals(ZonaEnvio.MISMA_PROVINCIA, ZonaEnvio.fromJson("misma"));
        assertEquals(ZonaEnvio.DISTINTA_PROVINCIA, ZonaEnvio.fromJson(" DISTINTA_PROVINCIA "));
        assertNull(ZonaEnvio.fromJson("CABA"));
    }

    static Usuario vendedor(Long id, String provincia) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre("Vendedor" + id);
        u.setProvincia(provincia);
        return u;
    }

    static Libro libro(Long id, Usuario vendedor) {
        Libro l = new Libro();
        l.setId(id);
        l.setVendedor(vendedor);
        return l;
    }

    static CarritoItem item(Libro libro, int cantidad) {
        return new CarritoItem(null, libro, cantidad);
    }
}