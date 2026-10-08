package com.uade.entrelibros.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.uade.entrelibros.backend.entity.EstadoModeracion;
import com.uade.entrelibros.backend.entity.EstadoPublicacion;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.LibroMioResponse;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.repository.CategoriaRepository;
import com.uade.entrelibros.backend.repository.HistorialModeracionRepository;
import com.uade.entrelibros.backend.repository.LibroCategoriaRepository;
import com.uade.entrelibros.backend.repository.LibroRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class LibroServiceImplMisLibrosTest {

    @Mock
    private LibroRepository libroRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private LibroCategoriaRepository libroCategoriaRepository;
    @Mock
    private HistorialModeracionRepository historialModeracionRepository;
    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private LibroServiceImpl libroService;

    private Usuario vendedor() {
        Usuario u = new Usuario();
        u.setId(7L);
        u.setRol(Rol.VENDEDOR);
        u.setNombre("Ana");
        return u;
    }

    private Libro libro(Long id, EstadoModeracion estado, Usuario vendedor) {
        Libro l = new Libro();
        l.setId(id);
        l.setEstadoModeracion(estado);
        l.setEstadoPublicacion(EstadoPublicacion.ACTIVA);
        l.setVendedor(vendedor);
        l.setSnapshotTitulo("Titulo aprobado " + id);
        return l;
    }

    private void devolver(Page<Libro> pagina) {
        when(libroRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pagina);
    }

    @Test
    void vendedorSinLibros() {
        devolver(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        ListaVaciaException ex = assertThrows(ListaVaciaException.class,
                () -> libroService.getLibrosMios(vendedor(), null, null, PageRequest.of(0, 20)));

        assertEquals("No tenés libros publicados", ex.getMessage());
        verify(libroRepository, never()).countByVendedorId(any());
    }

    @Test
    void paginaFueraDeRangoConFiltroNoDiceConEseEstado() {
        devolver(new PageImpl<>(List.of(), PageRequest.of(9, 20), 5));

        ListaVaciaException ex = assertThrows(ListaVaciaException.class, () -> libroService.getLibrosMios(
                vendedor(), EstadoModeracion.ACEPTADO, null, PageRequest.of(9, 20)));

        assertEquals("No tenés libros publicados", ex.getMessage());
    }

    @Test
    void filtroSinCoincidencias() {
        devolver(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
        when(libroRepository.countByVendedorId(7L)).thenReturn(3L);

        ListaVaciaException ex = assertThrows(ListaVaciaException.class, () -> libroService.getLibrosMios(
                vendedor(), EstadoModeracion.RECHAZADO, null, PageRequest.of(0, 20)));

        assertEquals("No tenés libros con ese estado", ex.getMessage());
    }

    @Test
    void motivoSoloParaRechazadosYConUnaConsulta() {
        Usuario v = vendedor();
        Libro rechazado = libro(1L, EstadoModeracion.RECHAZADO, v);
        Libro aceptado = libro(2L, EstadoModeracion.ACEPTADO, v);
        devolver(new PageImpl<>(List.of(rechazado, aceptado)));
        when(libroCategoriaRepository.findNombresPorLibroIds(anyList()))
                .thenReturn(List.<Object[]>of(new Object[] { 1L, "Ficción" }));
        when(historialModeracionRepository.findUltimoMotivoRechazoPorLibroIds(List.of(1L)))
                .thenReturn(List.<Object[]>of(new Object[] { 1L, "Fotos borrosas" }));

        List<LibroMioResponse> result = libroService.getLibrosMios(v, null, null, PageRequest.of(0, 20)).getContent();

        assertEquals("Fotos borrosas", result.get(0).getMotivoRechazo());
        assertNull(result.get(1).getMotivoRechazo());
        assertEquals(List.of("Ficción"), result.get(0).getCategorias());
        assertEquals(List.of(), result.get(1).getCategorias());
        // LibroMioResponse ahora conserva los campos de la moderacion
        assertEquals("Titulo aprobado 1", result.get(0).getSnapshotTitulo());
        verify(historialModeracionRepository, times(1)).findUltimoMotivoRechazoPorLibroIds(List.of(1L));
    }

    @Test
    void sinRechazadosNoConsultaElHistorial() {
        devolver(new PageImpl<>(List.of(libro(2L, EstadoModeracion.ACEPTADO, vendedor()))));
        when(libroCategoriaRepository.findNombresPorLibroIds(anyList())).thenReturn(List.of());

        libroService.getLibrosMios(vendedor(), null, null, PageRequest.of(0, 20));

        verifyNoInteractions(historialModeracionRepository);
    }
}