package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.Categoria;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.TipoNotificacion;
import com.uade.entrelibros.backend.exceptions.CategoriaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.CategoriaNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.repository.CategoriaRepository;
import com.uade.entrelibros.backend.repository.LibroCategoriaRepository;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private LibroCategoriaRepository libroCategoriaRepository;

    @Autowired
    private NotificacionService notificacionService;

    public List<Categoria> getCategorias() {
        List<Categoria> categorias = categoriaRepository.findByActivaTrueOrderByNombreAsc();
        if (categorias.isEmpty()) {
            throw new ListaVaciaException("No hay categorías registradas");
        }
        return categorias;
    }

    // Para el panel del admin: incluye las dadas de baja (getCategorias solo devuelve las activas).
    // Sin categorias devuelve [] y no ListaVaciaException: en el panel "todavia no hay ninguna" es un estado normal.
    @Override
    public List<Categoria> getTodasLasCategorias() {
        return categoriaRepository.findAllByOrderByNombreAsc();
    }

    public Categoria getCategoriaById(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(CategoriaNoEncontradaException::new);
    }

    public Categoria createCategoria(String nombre) {
        Categoria existente = categoriaRepository.findByNombre(nombre);
        if (existente != null)
            throw new CategoriaDuplicadaException();
        return categoriaRepository.save(new Categoria(nombre.trim()));
    }

    @Override
    public Categoria renombrarCategoria(Long categoriaId, String nombre) {
        Categoria categoria = getCategoriaById(categoriaId);
        Categoria existente = categoriaRepository.findByNombre(nombre.trim());
        if (existente != null && !existente.getId().equals(categoriaId)) {
            throw new CategoriaDuplicadaException();
        }
        categoria.setNombre(nombre.trim());
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public Categoria cambiarEstado(Long categoriaId, boolean activa) {
        Categoria categoria = getCategoriaById(categoriaId);
        boolean seDaDeBaja = categoria.isActiva() && !activa;
        categoria.setActiva(activa);
        Categoria guardada = categoriaRepository.save(categoria);
        if (seDaDeBaja) {
            notificarLibrosSinCategoria(guardada);
        }
        return guardada;
    }

    // Avisa al vendedor de cada libro que, con esta baja, se queda sin ninguna categoria activa.
    private void notificarLibrosSinCategoria(Categoria categoria) {
        for (Libro libro : libroCategoriaRepository.findLibrosSinOtraCategoriaActiva(categoria.getId())) {
            notificacionService.crear(libro.getVendedor(), TipoNotificacion.LIBRO_SIN_CATEGORIA,
                    "La categoría \"" + categoria.getNombre() + "\" se dio de baja y tu libro \"" + libro.getTitulo()
                            + "\" quedó sin categoría. Editalo para elegir otra.",
                    libro);
        }
    }
}