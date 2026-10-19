package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.EstadoPago;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.ResenaLibro;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.ResenaResumenResponse;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.CalificacionInvalidaException;
import com.uade.entrelibros.backend.exceptions.CampoInvalidoException;
import com.uade.entrelibros.backend.exceptions.CompraNoPagadaException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.OrdenItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ResenaDuplicadaException;
import com.uade.entrelibros.backend.exceptions.ResenaLibroNoEncontradaException;
import com.uade.entrelibros.backend.repository.LibroRepository;
import com.uade.entrelibros.backend.repository.OrdenItemRepository;
import com.uade.entrelibros.backend.repository.OrdenRepository;
import com.uade.entrelibros.backend.repository.ResenaLibroRepository;
import java.util.Comparator;

@Service
public class ResenaLibroServiceImpl implements ResenaLibroService {

    @Autowired
    private ResenaLibroRepository resenaLibroRepository;
    @Autowired
    private OrdenItemRepository ordenItemRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private com.uade.entrelibros.backend.repository.OrdenVendedorRepository ordenVendedorRepository;

    public List<ResenaLibro> getResenas() {
        List<ResenaLibro> resenas = resenaLibroRepository.findAll();
        if (resenas.isEmpty()) {
            throw new ListaVaciaException("No hay reseñas de libros registradas");
        }
        return resenas;
    }

    public ResenaLibro getResenaById(Long idResena) throws ResenaLibroNoEncontradaException {
        return resenaLibroRepository.findById(idResena)
                .orElseThrow(ResenaLibroNoEncontradaException::new);
    }

    public List<ResenaLibro> getResenasByLibro(Long idLibro, Integer calificacion, String sort) {
        Comparator<ResenaLibro> orden = switch (sort == null ? "" : sort) {
            case "antiguas" -> Comparator.comparing(ResenaLibro::getFecha);
            case "mejores"  -> Comparator.comparing(ResenaLibro::getCalificacion).reversed();
            case "peores"   -> Comparator.comparing(ResenaLibro::getCalificacion);
            default         -> Comparator.comparing(ResenaLibro::getFecha).reversed();
        };
        return resenaLibroRepository.findByLibroId(idLibro).stream()
                .filter(r -> calificacion == null || r.getCalificacion().equals(calificacion))
                .sorted(orden)
                .toList();
    }

    @Override
    public List<ResenaLibro> getResenasByVendedor(Long idVendedor) {
        List<ResenaLibro> resenas = resenaLibroRepository.findByVendedorIdOrderByFechaDesc(idVendedor);
        if (resenas.isEmpty()) {
            throw new ListaVaciaException("Ese vendedor todavía no tiene reseñas de libros");
        }
        return resenas;
    }

    public ResenaLibro crearResena(Usuario comprador, Long idOrdenItem, Integer calificacion, String comentario)
            throws OrdenItemNoEncontradoException, CalificacionInvalidaException, ResenaDuplicadaException,
            AccionNoPermitidaException {

        if (calificacion == null || calificacion < 1 || calificacion > 5)
            throw new CalificacionInvalidaException();
        validarComentario(comentario);

        OrdenItem ordenItem = ordenItemRepository.findById(idOrdenItem)
                .orElseThrow(OrdenItemNoEncontradoException::new);

        // Ownership: el que reseña tiene que ser el comprador real de ese item de orden
        if (ordenItem.getOrden() == null
                || ordenItem.getOrden().getComprador() == null
                || !ordenItem.getOrden().getComprador().getId().equals(comprador.getId())) {
            throw new AccionNoPermitidaException();
        }

        // Un vendedor puede comprar y reseñar, pero nunca un libro suyo
        if (ordenItem.getVendedor() != null && ordenItem.getVendedor().getId().equals(comprador.getId())) {
            throw new AccionNoPermitidaException();
        }

        if (ordenItem.getOrden().getEstadoPago() != EstadoPago.SIMULADO_APROBADO) {
            throw new CompraNoPagadaException();
        }
        // Si el vendedor cancelo su parte, ese libro no se compro
        if (!parteActiva(ordenItem)) {
            throw new AccionNoPermitidaException();
        }

        // Un libro comprado se puede resenar una sola vez por item de la orden
        if (!resenaLibroRepository.findByOrdenItemId(idOrdenItem).isEmpty())
            throw new ResenaDuplicadaException();

        return resenaLibroRepository.save(new ResenaLibro(ordenItem, calificacion, comentario));
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoResena getEstadoResena(Usuario usuario, Long idLibro) {
        Libro libro = libroRepository.findById(idLibro).orElseThrow(LibroNoEncontradoException::new);

        ResenaLibro miResena = resenaLibroRepository.findByLibroId(idLibro).stream()
                .filter(r -> esDelComprador(r, usuario))
                .max(Comparator.comparing(ResenaLibro::getFecha).thenComparing(ResenaLibro::getId))
                .orElse(null);

        if (usuario.getRol() == Rol.ADMIN) {
            return new EstadoResena(false, "Los administradores no pueden dejar reseñas.", null, miResena);
        }
        if (libro.getVendedor() != null && libro.getVendedor().getId().equals(usuario.getId())) {
            return new EstadoResena(false, "No podés reseñar tu propio libro.", null, miResena);
        }

        // Compras pagadas de este libro (los usados tambien se pueden reseñar)
        List<Long> idsOrdenes = ordenRepository.findByCompradorId(usuario.getId()).stream()
                .filter(o -> o.getEstadoPago() == EstadoPago.SIMULADO_APROBADO)
                .map(Orden::getId)
                .toList();
        List<OrdenItem> comprados = idsOrdenes.isEmpty()
                ? List.of()
                : ordenItemRepository.findByOrdenIdIn(idsOrdenes).stream()
                        .filter(i -> i.getLibro().getId().equals(idLibro))
                        .filter(this::parteActiva)
                        .toList();

        for (OrdenItem item : comprados) {
            if (resenaLibroRepository.findByOrdenItemId(item.getId()).isEmpty()) {
                return new EstadoResena(true, null, item.getId(), miResena);
            }
        }
        String motivo = comprados.isEmpty()
                ? "Solo pueden opinar quienes compraron y pagaron este libro."
                : "Ya opinaste sobre este libro.";
        return new EstadoResena(false, motivo, null, miResena);
    }

    @Override
    public ResenaResumenResponse getResumenByLibro(Long idLibro) {
        libroRepository.findById(idLibro).orElseThrow(LibroNoEncontradoException::new);
        return ResenaResumenResponse.de(resenaLibroRepository.findByLibroId(idLibro).stream()
                .map(ResenaLibro::getCalificacion)
                .toList());
    }

    @Override
    public ResenaResumenResponse getResumenByVendedor(Long idVendedor) {
        return ResenaResumenResponse.de(resenaLibroRepository.findByVendedorIdOrderByFechaDesc(idVendedor).stream()
                .map(ResenaLibro::getCalificacion)
                .toList());
    }

    private boolean parteActiva(OrdenItem item) {
        return ordenVendedorRepository.findByOrdenIdAndVendedorId(item.getOrden().getId(), item.getVendedor().getId())
                .map(ov -> ov.getEstado() == com.uade.entrelibros.backend.entity.EstadoOrdenVendedor.ACTIVA)
                .orElse(true);
    }

    private boolean esDelComprador(ResenaLibro resena, Usuario usuario) {
        return resena.getOrdenItem() != null
                && resena.getOrdenItem().getOrden() != null
                && resena.getOrdenItem().getOrden().getComprador() != null
                && resena.getOrdenItem().getOrden().getComprador().getId().equals(usuario.getId());
    }

    private void validarComentario(String comentario) {
        if (comentario == null || comentario.trim().length() < 3) {
            throw new CampoInvalidoException("comentario", "Contanos brevemente qué te pareció (mínimo 3 caracteres).");
        }
    }

    @Transactional
    public ResenaLibro modificarResena(Long idResena, Usuario comprador, Integer calificacion, String comentario) {
        ResenaLibro resena = getResenaById(idResena);
        validarAutor(resena, comprador);

        if (calificacion != null) {
            validarCalificacion(calificacion);
            resena.setCalificacion(calificacion);
        }
        if (comentario != null) {
            validarComentario(comentario);
            resena.setComentario(comentario);
        }

        return resenaLibroRepository.save(resena);
    }

    @Transactional
    public void eliminarResena(Long idResena, Usuario usuario) {
        ResenaLibro resena = getResenaById(idResena);
        // La borra su autor o un ADMIN (moderacion de contenido). Editar sigue siendo solo del autor:
        // el admin elimina, no reescribe lo que dijo otra persona.
        if (!esAdmin(usuario)) {
            validarAutor(resena, usuario);
        }
        resenaLibroRepository.delete(resena);
    }

    private boolean esAdmin(Usuario usuario) {
        return usuario != null && usuario.getRol() == Rol.ADMIN;
    }

    private void validarCalificacion(Integer calificacion) {
        if (calificacion < 1 || calificacion > 5) {
            throw new CalificacionInvalidaException();
        }
    }

    private void validarAutor(ResenaLibro resena, Usuario comprador) {
        if (comprador == null || resena.getOrdenItem() == null || resena.getOrdenItem().getOrden() == null
                || resena.getOrdenItem().getOrden().getComprador() == null
                || !resena.getOrdenItem().getOrden().getComprador().getId().equals(comprador.getId())) {
            throw new AccionNoPermitidaException();
        }
    }
}