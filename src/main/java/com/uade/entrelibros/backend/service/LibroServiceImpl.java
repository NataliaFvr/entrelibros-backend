package com.uade.entrelibros.backend.service;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.Categoria;
import com.uade.entrelibros.backend.entity.EstadoLibro;
import com.uade.entrelibros.backend.entity.EstadoModeracion;
import com.uade.entrelibros.backend.entity.EstadoPublicacion;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.LibroCategoria;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.TipoNotificacion;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.LibroFiltroRequest;
import com.uade.entrelibros.backend.entity.dto.LibroRequest;
import com.uade.entrelibros.backend.entity.dto.FiltrosDisponiblesResponse;
import com.uade.entrelibros.backend.entity.dto.VendedorOptionResponse;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.repository.CategoriaRepository;
import com.uade.entrelibros.backend.repository.LibroCategoriaRepository;
import com.uade.entrelibros.backend.repository.LibroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Pageable;
import com.uade.entrelibros.backend.entity.HistorialModeracion;
import com.uade.entrelibros.backend.repository.HistorialModeracionRepository;

@Service
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private LibroCategoriaRepository libroCategoriaRepository;

    @Autowired
    private HistorialModeracionRepository historialModeracionRepository;

    @Autowired
    private NotificacionService notificacionService;

    public Page<Libro> getLibros(PageRequest pageRequest) {
        Page<Libro> libros = libroRepository.findVisibles(pageRequest);
        if (libros.isEmpty()) {
            throw new ListaVaciaException("No hay libros disponibles");
        }
        return libros;
    }

    @Override
    public Page<Libro> buscarLibros(LibroFiltroRequest filtro, Pageable pageable) {
        Specification<Libro> spec = Specification.where(LibroSpecification.visibles())
            .and(LibroSpecification.contieneTexto(filtro.getTexto()))
            .and(LibroSpecification.tieneCategorias(filtro.getIdCategorias()))
            .and(LibroSpecification.precioMinimo(filtro.getPrecioMin()))
            .and(LibroSpecification.precioMaximo(filtro.getPrecioMax()))
            .and(LibroSpecification.enEditoriales(filtro.getEditoriales()))
            .and(LibroSpecification.enAutores(filtro.getAutores()))
            .and(LibroSpecification.enIdiomas(filtro.getIdiomas()))
            .and(LibroSpecification.enAnios(filtro.getAnios()))
            .and(LibroSpecification.conDescuento(filtro.getSoloConDescuento()))
            .and(LibroSpecification.deVendedores(filtro.getIdVendedores()))
            .and(LibroSpecification.enEstadosLibro(filtro.getEstadosLibro()))
            .and(LibroSpecification.envioLocal(filtro.getProvinciaComprador(), filtro.getEnvioLocal()));

        return libroRepository.findAll(spec, aplicarSort(pageable, filtro.getSort()));
    }

    // Traduce el parametro sort (String, mas simple de pasar por query param) a un Sort real de Spring Data,
    // preservando la pagina/tamanio que ya traia el Pageable original.
    private Pageable aplicarSort(Pageable pageable, String sort) {
        if (sort == null || sort.isBlank()) {
            return pageable;
        }
        Sort ordenamiento = switch (sort) {
            case "precioAsc" -> Sort.by("precio").ascending();
            case "precioDesc" -> Sort.by("precio").descending();
            case "descuento" -> Sort.by("descuentoPct").descending();
            case "nuevo" -> Sort.by("fechaPublicacion").descending();
            case "bestsellers" -> Sort.by("vendidos").descending();
            default -> Sort.unsorted();
        };
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), ordenamiento);
    }

    @Override
    public Page<Libro> getLibrosPorEstadoModeracion(EstadoModeracion estado, PageRequest pageRequest) {
        Page<Libro> libros = libroRepository.findByEstadoModeracionAndEstadoPublicacion(
                estado, EstadoPublicacion.ACTIVA, pageRequest);
        if (libros.isEmpty()) {
            throw new ListaVaciaException("No hay libros en ese estado de moderacion");
        }
        return libros;
    }

    public Libro getLibroById(Long libroId, Usuario usuario) throws LibroNoEncontradoException {
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(LibroNoEncontradoException::new);

        boolean esVisible = libro.getEstadoPublicacion() == EstadoPublicacion.ACTIVA
                && libro.getEstadoModeracion() == EstadoModeracion.ACEPTADO;
        boolean esDuenio = usuario != null && libro.getVendedor().getId().equals(usuario.getId());
        boolean esAdmin = usuario != null && usuario.getRol() == Rol.ADMIN;

        if (!esVisible && !esDuenio && !esAdmin) {
            // 404 en vez de 403 para no confirmar que el id existe a alguien sin permiso
            throw new LibroNoEncontradoException();
        }

        return libro;
    }

    // @Transactional: libro + categorias + notificacion a los admins se guardan juntos
    // o no se guarda nada (ej: si una categoria no existe, no queda el libro a medias)
    @Transactional
    public Libro createLibro(LibroRequest request, Usuario vendedor)
            throws CategoriaNoEncontradaException, RolInvalidoException {

        validarVendedor(vendedor);

        Libro libro = new Libro(request.getTitulo(), request.getAutor(), request.getEditorial(), request.getAnio(),
                request.getIdioma(), EstadoLibro.valueOf(request.getEstadoLibro()), request.getPrecio(),
                request.getDescuentoPct(), request.getStock(), request.getDescripcion(), vendedor);
        libro = libroRepository.save(libro);

        guardarCategorias(libro, request.getIdCategorias());

        notificarEnvioARevision(libro, vendedor, false);

        return libro;
    }

    @Transactional
    public Libro updateLibro(Long libroId, LibroRequest request, Usuario vendedor)
            throws LibroNoEncontradoException, CategoriaNoEncontradaException, RolInvalidoException,
            AccionNoPermitidaException {

        validarVendedor(vendedor);
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(LibroNoEncontradoException::new);
        validarDuenio(libro, vendedor);

        // Si se edita, vuelve a revision del admin
        boolean vuelveARevision = libro.getEstadoModeracion() != EstadoModeracion.EN_REVISION;
        if (vuelveARevision) {
            libro.guardarSnapshotActual();
        }
        aplicarCambios(libro, request);
        if (vuelveARevision) {
            libro.setEstadoModeracion(EstadoModeracion.EN_REVISION);
        }

        Libro actualizado = libroRepository.save(libro);

        if (request.getIdCategorias() != null) {
            libroCategoriaRepository.deleteAll(libroCategoriaRepository.findByLibroId(libroId));
            guardarCategorias(actualizado, request.getIdCategorias());
        }

        // Si ya estaba EN_REVISION los admins ya tienen su notificacion: no se duplica
        if (vuelveARevision) {
            notificarEnvioARevision(actualizado, vendedor, true);
        }

        return actualizado;
    }

    private void aplicarCambios(Libro libro, LibroRequest request) {
        setIfPresent(request.getTitulo(), libro::setTitulo);
        setIfPresent(request.getAutor(), libro::setAutor);
        setIfPresent(request.getEditorial(), libro::setEditorial);
        setIfPresent(request.getAnio(), libro::setAnio);
        setIfPresent(request.getIdioma(), libro::setIdioma);
        setIfPresent(request.getEstadoLibro(), v -> libro.setEstadoLibro(EstadoLibro.valueOf(v)));
        setIfPresent(request.getPrecio(), libro::setPrecio);
        setIfPresent(request.getDescuentoPct(), libro::setDescuentoPct);
        setIfPresent(request.getStock(), libro::setStock);
        setIfPresent(request.getDescripcion(), libro::setDescripcion);
    }

    private <T> void setIfPresent(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    // Baja logica: el libro no se borra, solo cambia su estado de publicacion
    public Libro darDeBajaLibro(Long libroId, Usuario vendedor)
            throws LibroNoEncontradoException, RolInvalidoException, AccionNoPermitidaException {
        return cambiarEstadoPublicacion(libroId, vendedor, EstadoPublicacion.DADA_DE_BAJA);
    }

    public Libro reactivarLibro(Long libroId, Usuario vendedor)
            throws LibroNoEncontradoException, RolInvalidoException, AccionNoPermitidaException {
        return cambiarEstadoPublicacion(libroId, vendedor, EstadoPublicacion.ACTIVA);
    }

    private Libro cambiarEstadoPublicacion(Long libroId, Usuario vendedor, EstadoPublicacion estado) {
        validarVendedor(vendedor);
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(LibroNoEncontradoException::new);
        validarDuenio(libro, vendedor);
        libro.setEstadoPublicacion(estado);
        return libroRepository.save(libro);
    }

    private void guardarCategorias(Libro libro, List<Long> idCategorias) throws CategoriaNoEncontradaException {
        if (idCategorias == null) {
            return;
        }

        for (Long idCategoria : idCategorias) {
            Categoria categoria = categoriaRepository.findById(idCategoria)
                    .orElseThrow(CategoriaNoEncontradaException::new);
            libroCategoriaRepository.save(new LibroCategoria(libro, categoria));
        }
    }

    private void validarVendedor(Usuario vendedor) throws RolInvalidoException {
        if (vendedor.getRol() != Rol.VENDEDOR) {
            throw new RolInvalidoException();
        }
    }

    private void validarDuenio(Libro libro, Usuario vendedor) throws AccionNoPermitidaException {
        if (!libro.getVendedor().getId().equals(vendedor.getId())) {
            throw new AccionNoPermitidaException();
        }
    }

    @Override
    @Transactional
    public Libro moderarLibro(Long libroId, EstadoModeracion estadoModeracion, String comentario, Usuario moderador)
            throws LibroNoEncontradoException {
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(LibroNoEncontradoException::new);

        EstadoModeracion estadoAnterior = libro.getEstadoModeracion();
        libro.setEstadoModeracion(estadoModeracion);
        Libro libroActualizado = libroRepository.save(libro);

        HistorialModeracion registro = new HistorialModeracion(
                libroActualizado, moderador, estadoAnterior, estadoModeracion, comentario);
        historialModeracionRepository.save(registro);


        if (estadoAnterior != estadoModeracion) {
            notificarModeracion(libroActualizado, estadoModeracion, comentario);
        }

        return libroActualizado;
    }

    @Override
    public FiltrosDisponiblesResponse getFiltrosDisponibles() {
        FiltrosDisponiblesResponse r = new FiltrosDisponiblesResponse();
        r.setEditoriales(libroRepository.findEditorialesDistintas());
        r.setAutores(libroRepository.findAutoresDistintos());
        r.setIdiomas(libroRepository.findIdiomasDistintos());
        r.setEstadosLibro(java.util.Arrays.stream(EstadoLibro.values()).map(Enum::name).toList());
        r.setVendedores(libroRepository.findVendedoresConLibrosVisibles().stream()
                .map(v -> new VendedorOptionResponse(
                        v.getId(),
                        v.getNombreTienda() != null && !v.getNombreTienda().isBlank()
                                ? v.getNombreTienda()
                                : v.getNombre() + " " + v.getApellido()))
                .toList());
        return r;
    }

    private void notificarEnvioARevision(Libro libro, Usuario vendedor, boolean esEdicion) {
        String accion = esEdicion ? "modifico y reenvio" : "envio";
        String mensaje = vendedor.getNombre() + " " + vendedor.getApellido() + " " + accion
                + " el libro \"" + libro.getTitulo() + "\" para su revision";
        notificacionService.notificarAdmins(TipoNotificacion.LIBRO_PENDIENTE_REVISION, mensaje, libro);
    }

    private void notificarModeracion(Libro libro, EstadoModeracion estado, String comentario) {
        if (estado == EstadoModeracion.ACEPTADO) {
            notificacionService.crear(libro.getVendedor(), TipoNotificacion.LIBRO_ACEPTADO,
                    "Tu libro \"" + libro.getTitulo() + "\" fue aprobado y ya esta publicado", libro);
        } else if (estado == EstadoModeracion.RECHAZADO) {
            String motivo = comentario != null && !comentario.isBlank() ? " Motivo: " + comentario : "";
            notificacionService.crear(libro.getVendedor(), TipoNotificacion.LIBRO_RECHAZADO,
                    "Tu libro \"" + libro.getTitulo() + "\" fue rechazado." + motivo, libro);
        }
    }

}