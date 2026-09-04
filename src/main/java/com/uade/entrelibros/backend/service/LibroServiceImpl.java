package com.uade.entrelibros.backend.service;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Categoria;
import com.uade.entrelibros.backend.entity.EstadoLibro;
import com.uade.entrelibros.backend.entity.EstadoModeracion;
import com.uade.entrelibros.backend.entity.EstadoPublicacion;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.LibroCategoria;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.LibroFiltroRequest;
import com.uade.entrelibros.backend.entity.dto.LibroRequest;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.repository.CategoriaRepository;
import com.uade.entrelibros.backend.repository.LibroCategoriaRepository;
import com.uade.entrelibros.backend.repository.LibroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public Page<Libro> getLibros(PageRequest pageRequest) {
        return libroRepository.findVisibles(pageRequest);
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
            .and(LibroSpecification.deVendedores(filtro.getIdVendedores()));

        return libroRepository.findAll(spec, pageable);
    }

    public Libro getLibroById(Long libroId) throws LibroNoEncontradoException {
        return libroRepository.findById(libroId)
                .orElseThrow(LibroNoEncontradoException::new);
    }

    public Libro createLibro(LibroRequest request, Usuario vendedor)
            throws CategoriaNoEncontradaException, RolInvalidoException {

        validarVendedor(vendedor);

        Libro libro = new Libro(request.getTitulo(), request.getAutor(), request.getEditorial(), request.getAnio(),
                request.getIdioma(), EstadoLibro.valueOf(request.getEstadoLibro()), request.getPrecio(),
                request.getDescuentoPct(), request.getStock(), request.getDescripcion(), vendedor);
        libro = libroRepository.save(libro);

        guardarCategorias(libro, request.getIdCategorias());

        return libro;
    }

   public Libro updateLibro(Long libroId, LibroRequest request, Usuario vendedor)
        throws LibroNoEncontradoException, CategoriaNoEncontradaException, RolInvalidoException,
        AccionNoPermitidaException {

        validarVendedor(vendedor);
        Libro libro = libroRepository.findById(libroId)
            .orElseThrow(LibroNoEncontradoException::new);
        validarDuenio(libro, vendedor);

        aplicarCambios(libro, request);

        Libro actualizado = libroRepository.save(libro);

        if (request.getIdCategorias() != null) {
            libroCategoriaRepository.deleteAll(libroCategoriaRepository.findByLibroId(libroId));
            guardarCategorias(actualizado, request.getIdCategorias());
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

    public void darDeBajaLibro(Long libroId, Usuario vendedor)
            throws LibroNoEncontradoException, RolInvalidoException, AccionNoPermitidaException {
        validarVendedor(vendedor);
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(LibroNoEncontradoException::new);
        validarDuenio(libro, vendedor);
        libro.setEstadoPublicacion(EstadoPublicacion.DADA_DE_BAJA);
        libroRepository.save(libro);
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

        // DESPUÉS:
    @Override
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

        return libroActualizado;
    }



}
