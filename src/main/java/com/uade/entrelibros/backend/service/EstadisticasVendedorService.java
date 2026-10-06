package com.uade.entrelibros.backend.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.LibroCategoria;
import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.EstadisticasVendedorResponse;
import com.uade.entrelibros.backend.entity.dto.EstadisticasVendedorResponse.Desglose;
import com.uade.entrelibros.backend.repository.LibroCategoriaRepository;
import com.uade.entrelibros.backend.repository.OrdenItemRepository;

@Service
public class EstadisticasVendedorService {

    @Autowired
    private OrdenItemRepository ordenItemRepository;

    @Autowired
    private LibroCategoriaRepository libroCategoriaRepository;

    public EstadisticasVendedorResponse getEstadisticas(Usuario vendedor) {
        List<OrdenItem> items = ordenItemRepository.findVentasConfirmadasByVendedorId(vendedor.getId());

        double ingresos = 0.0;
        long unidades = 0;
        Set<Long> ordenes = new HashSet<>();
        Map<String, Desglose> porCategoria = new LinkedHashMap<>();
        Map<String, Desglose> porEstado = new LinkedHashMap<>();

        for (OrdenItem item : items) {
            double monto = item.getCantidad() * item.getPrecioUnitario();
            ingresos += monto;
            unidades += item.getCantidad();
            ordenes.add(item.getOrden().getId());

            if (item.getLibro().getEstadoLibro() != null) {
                acumular(porEstado, item.getLibro().getEstadoLibro().name(), item.getCantidad(), monto);
            }
            for (LibroCategoria lc : libroCategoriaRepository.findByLibroId(item.getLibro().getId())) {
                acumular(porCategoria, lc.getCategoria().getNombre(), item.getCantidad(), monto);
            }
        }

        EstadisticasVendedorResponse r = new EstadisticasVendedorResponse();
        r.setIngresosTotales(ingresos);
        r.setUnidadesVendidas(unidades);
        r.setCantidadVentas(ordenes.size());
        r.setPromedioPorVenta(ordenes.isEmpty() ? 0.0 : ingresos / ordenes.size());
        r.setVentasPorCategoria(new ArrayList<>(porCategoria.values()));
        r.setVentasPorEstado(new ArrayList<>(porEstado.values()));
        return r;
    }

    private void acumular(Map<String, Desglose> mapa, String nombre, int unidades, double monto) {
        Desglose d = mapa.computeIfAbsent(nombre, n -> new Desglose(n, 0L, 0.0));
        d.setUnidades(d.getUnidades() + unidades);
        d.setIngresos(d.getIngresos() + monto);
    }
}