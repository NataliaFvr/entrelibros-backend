package com.uade.entrelibros.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.LibroFiltroRequest;
import com.uade.entrelibros.backend.entity.dto.LibroRequest;
import com.uade.entrelibros.backend.entity.dto.LibroResponse;
import com.uade.entrelibros.backend.entity.dto.FiltrosDisponiblesResponse;
import com.uade.entrelibros.backend.entity.EstadoModeracion;
import com.uade.entrelibros.backend.entity.dto.ModeracionRequest;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.EstadoFiltroInvalidoException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.service.LibroService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import com.uade.entrelibros.backend.entity.dto.HistorialModeracionResponse;
import com.uade.entrelibros.backend.service.HistorialModeracionService;
import com.uade.entrelibros.backend.entity.EstadoPublicacion;
import com.uade.entrelibros.backend.entity.dto.LibroMioResponse;
import com.uade.entrelibros.backend.repository.ImagenLibroRepository;
import com.uade.entrelibros.backend.repository.LibroCategoriaRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("libros")
public class LibrosController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private HistorialModeracionService historialModeracionService;

    @Autowired
    private LibroCategoriaRepository libroCategoriaRepository;

    @Autowired
    private ImagenLibroRepository imagenLibroRepository;

    @GetMapping
    public ResponseEntity<Page<LibroResponse>> getLibros(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) List<Long> idCategorias,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) Integer anioMin,
            @RequestParam(required = false) Integer anioMax,
            @RequestParam(required = false) Double descuentoMin,
            @RequestParam(required = false) List<String> editoriales,
            @RequestParam(required = false) List<String> autores,
            @RequestParam(required = false) List<String> idiomas,
            @RequestParam(required = false) List<Integer> anios,
            @RequestParam(required = false) Boolean soloConDescuento,
            @RequestParam(required = false) List<Long> idVendedores,
            @RequestParam(required = false) List<String> estadoLibro,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String provinciaComprador,
            @RequestParam(required = false) Boolean envioLocal,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        LibroFiltroRequest filtro = new LibroFiltroRequest();
        filtro.setTexto(texto);
        filtro.setIdCategorias(idCategorias);
        filtro.setPrecioMin(precioMin);
        filtro.setPrecioMax(precioMax);
        filtro.setAnioMin(anioMin);
        filtro.setAnioMax(anioMax);
        filtro.setDescuentoMin(descuentoMin);
        filtro.setEditoriales(editoriales);
        filtro.setAutores(autores);
        filtro.setIdiomas(idiomas);
        filtro.setAnios(anios);
        filtro.setSoloConDescuento(soloConDescuento);
        filtro.setIdVendedores(idVendedores);
        filtro.setEstadosLibro(estadoLibro);
        filtro.setSort(sort);
        filtro.setProvinciaComprador(provinciaComprador);
        filtro.setEnvioLocal(envioLocal);

        Page<Libro> libros = libroService.buscarLibros(filtro, PageRequest.of(page, size));
        Map<Long, List<String>> categorias = categoriasPorLibro(libros.getContent());
        Map<Long, Long> portadas = portadasPorLibro(libros.getContent());
        return ResponseEntity.ok(libros.map(libro -> LibroResponse.from(
                libro, categorias.getOrDefault(libro.getId(), List.of()), provinciaComprador)
                .conPortada(portadas.get(libro.getId()))));
    }

    // Libros del vendedor autenticado en TODOS sus estados. /mios es literal y gana sobre /{libroId}.
    // Sin resultados -> ListaVaciaException (404). COMPRADOR/ADMIN -> 403 por @PreAuthorize.
    // Los estados llegan como texto para devolver {"error": ...} si son invalidos y aceptar PENDIENTE = EN_REVISION.
    @PreAuthorize("hasAuthority('VENDEDOR')")
    @GetMapping("/mios")
    public ResponseEntity<Page<LibroMioResponse>> getMisLibros(
            @AuthenticationPrincipal Usuario vendedor,
            @RequestParam(required = false) String estadoModeracion,
            @RequestParam(required = false) String estadoPublicacion,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String sort) throws EstadoFiltroInvalidoException {
        EstadoModeracion moderacion = parsearModeracion(estadoModeracion);
        EstadoPublicacion publicacion = parsearPublicacion(estadoPublicacion);
        if (page < 0) {
            throw new ListaVaciaException("No tenés libros publicados");
        }
        int tamanio = Math.min(Math.max(size, 1), 100);
        Sort sortConfig = switch (sort == null ? "" : sort.trim()) {
            case "precioAsc" -> Sort.by("precio").ascending();
            case "precioDesc" -> Sort.by("precio").descending();
            case "descuento" -> Sort.by("descuentoPct").descending();
            case "bestsellers" -> Sort.by("vendidos").descending();
            default -> Sort.by("fechaPublicacion").descending(); // "nuevo" o sin sort: mas nuevos primero
        };
        // desempate por id: la paginacion queda estable aunque haya fechas iguales
        sortConfig = sortConfig.and(Sort.by("id").descending());
        Page<LibroMioResponse> mios = libroService.getLibrosMios(
                vendedor, moderacion, publicacion, PageRequest.of(page, tamanio, sortConfig));
        Map<Long, Long> portadas = portadasPorIds(mios.getContent().stream().map(LibroMioResponse::getId).toList());
        mios.forEach(libro -> libro.conPortada(portadas.get(libro.getId())));
        return ResponseEntity.ok(mios);
    }

    private EstadoModeracion parsearModeracion(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String v = valor.trim().toUpperCase();
        if (v.equals("PENDIENTE")) {
            return EstadoModeracion.EN_REVISION;
        }
        try {
            return EstadoModeracion.valueOf(v);
        } catch (IllegalArgumentException e) {
            throw new EstadoFiltroInvalidoException();
        }
    }

    private EstadoPublicacion parsearPublicacion(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return switch (valor.trim().toUpperCase()) {
            case "ACTIVA", "ACTIVO" -> EstadoPublicacion.ACTIVA;
            case "DADA_DE_BAJA", "DADO_DE_BAJA", "BAJA" -> EstadoPublicacion.DADA_DE_BAJA;
            default -> throw new EstadoFiltroInvalidoException();
        };
    }

    // Valores unicos para poblar los <select> de filtro del catalogo (Editorial, Autor, Idioma, Vendedor, Estado)
    @GetMapping("/filtros-disponibles")
    public ResponseEntity<FiltrosDisponiblesResponse> getFiltrosDisponibles() {
        return ResponseEntity.ok(libroService.getFiltrosDisponibles());
    }

    @GetMapping("/{libroId}")
    public ResponseEntity<LibroResponse> getLibroById(
            @AuthenticationPrincipal(errorOnInvalidType = false) Usuario usuario,
            @PathVariable Long libroId,
            @RequestParam(required = false) String provinciaComprador)
            throws LibroNoEncontradoException {
        Libro libro = libroService.getLibroById(libroId, usuario);
        return ResponseEntity.ok(LibroResponse.from(libro, categoriasDe(libro), provinciaComprador)
                .conPortada(portadaDe(libro)));
    }

    @PreAuthorize("hasAuthority('VENDEDOR')")
    @PostMapping
    public ResponseEntity<LibroResponse> createLibro(
            @AuthenticationPrincipal Usuario vendedor,
            @Validated(LibroRequest.Crear.class) @RequestBody LibroRequest request)
            throws CategoriaNoEncontradaException, RolInvalidoException {
        Libro result = libroService.createLibro(request, vendedor);
        return ResponseEntity.created(URI.create("/libros/" + result.getId()))
                .body(LibroResponse.from(result, categoriasDe(result)));
    }

    @PreAuthorize("hasAuthority('VENDEDOR')")
    @PatchMapping("/{libroId}")
    public ResponseEntity<LibroResponse> updateLibro(
            @AuthenticationPrincipal Usuario vendedor,
            @PathVariable Long libroId,
            @Validated(LibroRequest.Actualizar.class) @RequestBody LibroRequest request)
            throws LibroNoEncontradoException, CategoriaNoEncontradaException, RolInvalidoException,
            AccionNoPermitidaException {
        Libro result = libroService.updateLibro(libroId, request, vendedor);
        return ResponseEntity.ok(respuesta(result));
    }

    @PreAuthorize("hasAuthority('VENDEDOR')")
    @PatchMapping("/{libroId}/baja")
    public ResponseEntity<LibroResponse> darDeBajaLibro(
            @AuthenticationPrincipal Usuario vendedor,
            @PathVariable Long libroId)
            throws LibroNoEncontradoException, RolInvalidoException, AccionNoPermitidaException {
        Libro result = libroService.darDeBajaLibro(libroId, vendedor);
        return ResponseEntity.ok(respuesta(result));
    }

    @PreAuthorize("hasAuthority('VENDEDOR')")
    @PatchMapping("/{libroId}/reactivar")
    public ResponseEntity<LibroResponse> reactivarLibro(
            @AuthenticationPrincipal Usuario vendedor,
            @PathVariable Long libroId)
            throws LibroNoEncontradoException, RolInvalidoException, AccionNoPermitidaException {
        Libro result = libroService.reactivarLibro(libroId, vendedor);
        return ResponseEntity.ok(respuesta(result));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{libroId}/moderacion")
    public ResponseEntity<LibroResponse> moderarLibro(
            @PathVariable Long libroId,
            @AuthenticationPrincipal Usuario moderador,
            @RequestBody ModeracionRequest request)
        throws LibroNoEncontradoException {
        Libro result = libroService.moderarLibro(
                libroId, request.getEstadoModeracion(), request.getComentario(), moderador);
        return ResponseEntity.ok(respuesta(result));
    }

    // Listado de moderacion SOLO para el admin. No toca el catalogo (visibles()):
    //   GET /libros/moderacion?estado=EN_REVISION  -> los pendientes de revisar
    //   GET /libros/moderacion?estado=ACEPTADO     -> los ya aprobados/activos
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/moderacion")
    public ResponseEntity<Page<LibroResponse>> getLibrosPorModeracion(
            @RequestParam EstadoModeracion estado,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<Libro> libros = libroService.getLibrosPorEstadoModeracion(estado, PageRequest.of(page, size));
        Map<Long, List<String>> categorias = categoriasPorLibro(libros.getContent());
        Map<Long, Long> portadas = portadasPorLibro(libros.getContent());
        return ResponseEntity.ok(libros.map(libro -> LibroResponse.from(
                libro, categorias.getOrDefault(libro.getId(), List.of()))
                .conPortada(portadas.get(libro.getId()))));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{libroId}/historial-moderacion")
    public ResponseEntity<Page<HistorialModeracionResponse>> getHistorialModeracionPorLibro(
            @PathVariable Long libroId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) throws LibroNoEncontradoException {
        return ResponseEntity.ok(historialModeracionService.getHistorialPorLibro(
                libroId, PageRequest.of(page, size)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/historial-moderacion")
    public ResponseEntity<Page<HistorialModeracionResponse>> getHistorialModeracionCompleto(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ResponseEntity.ok(historialModeracionService.getHistorialCompleto(PageRequest.of(page, size)));
    }

    // LibroResponse completo (categorias + portada) para un solo libro
    private LibroResponse respuesta(Libro libro) {
        return LibroResponse.from(libro, categoriasDe(libro)).conPortada(portadaDe(libro));
    }

    // Una consulta que trae SOLO ids (nunca el LONGBLOB): filas ordenadas por libro y orden,
    // asi que la primera de cada libro es su portada. Una consulta por pagina, no una por libro.
    private Map<Long, Long> portadasPorLibro(List<Libro> libros) {
        return portadasPorIds(libros.stream().map(Libro::getId).toList());
    }

    private Map<Long, Long> portadasPorIds(List<Long> idsLibros) {
        if (idsLibros.isEmpty()) {
            return Map.of();
        }
        Map<Long, Long> portadas = new HashMap<>();
        for (Object[] fila : imagenLibroRepository.findIdsPorLibroIds(idsLibros)) {
            portadas.putIfAbsent((Long) fila[0], (Long) fila[1]);
        }
        return portadas;
    }

    private Long portadaDe(Libro libro) {
        return portadasPorLibro(List.of(libro)).get(libro.getId());
    }

    // Una consulta escalar (antes cargaba cada LibroCategoria con su libro y categoria EAGER = selects extra)
    private List<String> categoriasDe(Libro libro) {
        return categoriasPorLibro(List.of(libro)).getOrDefault(libro.getId(), List.of());
    }

    private Map<Long, List<String>> categoriasPorLibro(List<Libro> libros) {
        if (libros.isEmpty()) {
            return Map.of();
        }
        return libroCategoriaRepository.findNombresPorLibroIds(libros.stream().map(Libro::getId).toList()).stream()
                .collect(Collectors.groupingBy(fila -> (Long) fila[0],
                        Collectors.mapping(fila -> (String) fila[1], Collectors.toList())));
    }
}