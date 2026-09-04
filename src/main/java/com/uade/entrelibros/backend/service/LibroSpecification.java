package com.uade.entrelibros.backend.service;

import java.util.List;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import com.uade.entrelibros.backend.entity.EstadoPublicacion;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.LibroCategoria;

public class LibroSpecification {

    // se aplica siempre, sea cual sea el resto de los filtros: solo libros publicados
    public static Specification<Libro> visibles() {
        return (root, query, cb) -> cb.equal(root.get("estadoPublicacion"), EstadoPublicacion.ACTIVA);
    }

    public static Specification<Libro> contieneTexto(String texto) {
        return (root, query, cb) -> {
            if (texto == null || texto.isBlank()) return cb.conjunction();
            String like = "%" + texto.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("titulo")), like),
                cb.like(cb.lower(root.get("autor")), like),
                cb.like(cb.lower(root.get("descripcion")), like)
            );
        };
    }

    public static Specification<Libro> tieneCategorias(List<Long> idCategorias) {
        return (root, query, cb) -> {
            if (idCategorias == null || idCategorias.isEmpty()) return cb.conjunction();
            Subquery<Long> sub = query.subquery(Long.class);
            var lc = sub.from(LibroCategoria.class);
            sub.select(lc.get("libro").get("id"))
               .where(lc.get("categoria").get("id").in(idCategorias));
            return root.get("id").in(sub);
        };
    }

    public static Specification<Libro> precioMinimo(Double min) {
        return (root, query, cb) -> min == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("precio"), min);
    }

    public static Specification<Libro> precioMaximo(Double max) {
        return (root, query, cb) -> max == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("precio"), max);
    }

    public static Specification<Libro> enEditoriales(List<String> editoriales) {
        return (root, query, cb) -> (editoriales == null || editoriales.isEmpty())
                ? cb.conjunction() : root.get("editorial").in(editoriales);
    }

    public static Specification<Libro> enAutores(List<String> autores) {
        return (root, query, cb) -> (autores == null || autores.isEmpty())
                ? cb.conjunction() : root.get("autor").in(autores);
    }

    public static Specification<Libro> enIdiomas(List<String> idiomas) {
        return (root, query, cb) -> (idiomas == null || idiomas.isEmpty())
                ? cb.conjunction() : root.get("idioma").in(idiomas);
    }

    public static Specification<Libro> enAnios(List<Integer> anios) {
        return (root, query, cb) -> (anios == null || anios.isEmpty())
                ? cb.conjunction() : root.get("anio").in(anios);
    }

    public static Specification<Libro> conDescuento(Boolean soloConDescuento) {
        return (root, query, cb) -> (soloConDescuento == null || !soloConDescuento)
                ? cb.conjunction() : cb.greaterThan(root.get("descuentoPct"), 0.0);
    }

    public static Specification<Libro> deVendedores(List<Long> idVendedores) {
        return (root, query, cb) -> (idVendedores == null || idVendedores.isEmpty())
                ? cb.conjunction() : root.get("vendedor").get("id").in(idVendedores);
    }
}