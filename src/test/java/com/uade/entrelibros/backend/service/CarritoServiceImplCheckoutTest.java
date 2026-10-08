package com.uade.entrelibros.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.uade.entrelibros.backend.entity.Carrito;
import com.uade.entrelibros.backend.entity.CarritoItem;
import com.uade.entrelibros.backend.entity.EstadoModeracion;
import com.uade.entrelibros.backend.entity.EstadoPublicacion;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.OrdenVendedor;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.ZonaEnvio;
import com.uade.entrelibros.backend.exceptions.CompraPropiaException;
import com.uade.entrelibros.backend.repository.CarritoItemRepository;
import com.uade.entrelibros.backend.repository.CarritoRepository;
import com.uade.entrelibros.backend.repository.DireccionRepository;
import com.uade.entrelibros.backend.repository.LibroRepository;
import com.uade.entrelibros.backend.repository.OrdenItemRepository;
import com.uade.entrelibros.backend.repository.OrdenRepository;
import com.uade.entrelibros.backend.repository.OrdenVendedorRepository;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class CarritoServiceImplCheckoutTest {

    private static final Long ID_COMPRADOR = 99L;

    @Mock
    private CarritoRepository carritoRepository;
    @Mock
    private CarritoItemRepository carritoItemRepository;
    @Mock
    private LibroRepository libroRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private OrdenRepository ordenRepository;
    @Mock
    private OrdenVendedorRepository ordenVendedorRepository;
    @Mock
    private OrdenItemRepository ordenItemRepository;
    @Mock
    private DireccionRepository direccionRepository;
    @Mock
    private EnvioService envioService;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    private Carrito carrito;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(carritoService, "envioPolicy", new EnvioPolicy(envioService));
        carrito = new Carrito(EnvioPolicyTest.vendedor(ID_COMPRADOR, "Córdoba"));
        carrito.setId(1L);
    }

    private Libro libro(Long id, Usuario vendedor, double precio, Double descuento) {
        Libro l = EnvioPolicyTest.libro(id, vendedor);
        l.setPrecio(precio);
        l.setDescuentoPct(descuento);
        l.setStock(10);
        l.setVendidos(0);
        l.setEstadoPublicacion(EstadoPublicacion.ACTIVA);
        l.setEstadoModeracion(EstadoModeracion.ACEPTADO);
        when(libroRepository.findByIdConCandado(id)).thenReturn(Optional.of(l));
        return l;
    }

    @Test
    void unEnvioPorVendedorYTotalIgualAlDelFront() {
        Usuario porteno = EnvioPolicyTest.vendedor(1L, "Ciudad Autónoma de Buenos Aires");
        Usuario cordobes = EnvioPolicyTest.vendedor(2L, "Córdoba");
        Libro l1 = libro(10L, porteno, 1000.0, 15.0);   // 850.00
        Libro l2 = libro(11L, porteno, 333.33, 10.0);   // 299.997 -> 300.00
        Libro l3 = libro(12L, cordobes, 2000.0, null);  // 2000.00
        when(carritoRepository.findByUsuarioId(ID_COMPRADOR)).thenReturn(carrito);
        when(carritoItemRepository.findByCarritoId(1L)).thenReturn(List.of(
                new CarritoItem(carrito, l1, 2), new CarritoItem(carrito, l2, 1), new CarritoItem(carrito, l3, 1)));
        when(envioService.getCostoPorZona(ZonaEnvio.MISMA_PROVINCIA)).thenReturn(1800.0);
        when(envioService.getCostoPorZona(ZonaEnvio.DISTINTA_PROVINCIA)).thenReturn(3500.0);
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ordenVendedorRepository.save(any(OrdenVendedor.class))).thenAnswer(inv -> inv.getArgument(0));

        // el front manda "CABA" (provinciaParaBack): tiene que dar MISMA contra "Ciudad Autónoma de Buenos Aires"
        Orden orden = carritoService.checkout(ID_COMPRADOR, null, "CABA");

        assertEquals(4000.0, orden.getSubtotal());
        assertEquals(5300.0, orden.getCostoEnvio());
        assertEquals(9300.0, orden.getTotal());
        ArgumentCaptor<OrdenVendedor> captor = ArgumentCaptor.forClass(OrdenVendedor.class);
        verify(ordenVendedorRepository, times(2)).save(captor.capture());
        assertEquals(ZonaEnvio.MISMA_PROVINCIA, captor.getAllValues().get(0).getZonaEnvio());
        assertEquals(1800.0, captor.getAllValues().get(0).getCostoEnvio());
        assertEquals(ZonaEnvio.DISTINTA_PROVINCIA, captor.getAllValues().get(1).getZonaEnvio());
        assertEquals(3500.0, captor.getAllValues().get(1).getCostoEnvio());
    }

    @Test
    void noSePuedeComprarUnLibroPropio() {
        Libro propio = libro(10L, EnvioPolicyTest.vendedor(ID_COMPRADOR, "Córdoba"), 100.0, 0.0);
        when(carritoRepository.findByUsuarioId(ID_COMPRADOR)).thenReturn(carrito);
        when(carritoItemRepository.findByCarritoId(1L)).thenReturn(List.of(new CarritoItem(carrito, propio, 1)));

        assertThrows(CompraPropiaException.class, () -> carritoService.checkout(ID_COMPRADOR, null, "Córdoba"));
        verify(ordenRepository, never()).save(any());
    }

    @Test
    void precioConDescuentoRedondeaComoElFront() {
        Libro l = new Libro();
        l.setPrecio(19.99);
        l.setDescuentoPct(33.0);
        assertEquals(13.39, CarritoServiceImpl.precioConDescuento(l));
        l.setPrecio(1.005);
        l.setDescuentoPct(null);
        // JS: Math.round(1.005 * 1 * 100) / 100 === 1 (BigDecimal daba 1.01)
        assertEquals(1.0, CarritoServiceImpl.precioConDescuento(l));
    }
}