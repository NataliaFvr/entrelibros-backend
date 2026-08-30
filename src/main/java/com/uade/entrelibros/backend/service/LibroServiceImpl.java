package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Categoria;
import com.uade.entrelibros.backend.entity.EstadoLibro;
import com.uade.entrelibros.backend.entity.EstadoPublicacion;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.LibroCategoria;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.LibroRequest;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
import com.uade.entrelibros.backend.repository.CategoriaRepository;
import com.uade.entrelibros.backend.repository.LibroCategoriaRepository;
import com.uade.entrelibros.backend.repository.LibroRepository;

@Service
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private LibroCategoriaRepository libroCategoriaRepository;

    public List<Libro> getLibros() {
        return libroRepository.findVisibles();
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

        if (request.getTitulo() != null)
            libro.setTitulo(request.getTitulo());
        if (request.getAutor() != null)
            libro.setAutor(request.getAutor());
        if (request.getEditorial() != null)
            libro.setEditorial(request.getEditorial());
        if (request.getAnio() != null)
            libro.setAnio(request.getAnio());
        if (request.getIdioma() != null)
            libro.setIdioma(request.getIdioma());
        if (request.getEstadoLibro() != null)
            libro.setEstadoLibro(EstadoLibro.valueOf(request.getEstadoLibro()));
        if (request.getPrecio() != null)
            libro.setPrecio(request.getPrecio());
        if (request.getDescuentoPct() != null)
            libro.setDescuentoPct(request.getDescuentoPct());
        if (request.getStock() != null)
            libro.setStock(request.getStock());
        if (request.getDescripcion() != null)
            libro.setDescripcion(request.getDescripcion());

        Libro actualizado = libroRepository.save(libro);

        if (request.getIdCategorias() != null) {
            libroCategoriaRepository.deleteAll(libroCategoriaRepository.findByLibroId(libroId));
            guardarCategorias(actualizado, request.getIdCategorias());
        }

        return actualizado;
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
}
