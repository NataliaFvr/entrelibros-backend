package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Categoria;
import com.uade.entrelibros.backend.entity.EstadoLibro;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.LibroCategoria;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.VendedorNoEncontradoException;
import com.uade.entrelibros.backend.repository.CategoriaRepository;
import com.uade.entrelibros.backend.repository.LibroCategoriaRepository;
import com.uade.entrelibros.backend.repository.LibroRepository;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

@Service
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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

    public Libro createLibro(String titulo, String autor, String editorial, Integer anio, String idioma,
            String estadoLibro, Double precio, Double descuentoPct, Integer stock, String descripcion,
            Long idVendedor, List<Long> idCategorias)
            throws VendedorNoEncontradoException, CategoriaNoEncontradaException {

        Usuario vendedor = usuarioRepository.findById(idVendedor)
                .orElseThrow(VendedorNoEncontradoException::new);

        Libro libro = new Libro(titulo, autor, editorial, anio, idioma,
                EstadoLibro.valueOf(estadoLibro), precio, descuentoPct, stock, descripcion, vendedor);
        libro = libroRepository.save(libro);

        for (Long idCategoria : idCategorias) {
            Categoria categoria = categoriaRepository.findById(idCategoria)
                    .orElseThrow(CategoriaNoEncontradaException::new);
            libroCategoriaRepository.save(new LibroCategoria(libro, categoria));
        }

        return libro;
    }
}
