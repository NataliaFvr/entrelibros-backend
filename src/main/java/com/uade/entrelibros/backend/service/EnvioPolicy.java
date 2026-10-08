package com.uade.entrelibros.backend.service;

import java.text.Normalizer;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.uade.entrelibros.backend.entity.CarritoItem;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.ZonaEnvio;

import lombok.RequiredArgsConstructor;

// Unica fuente de verdad de las reglas de envio (las mismas que utils/adaptadores.tipoEnvio del front):
//  - MISMA_PROVINCIA si vendedor y comprador estan en la misma provincia, comparadas sin tildes, sin mayusculas
//    y sin espacios de mas; si falta cualquiera de las dos -> DISTINTA_PROVINCIA.
//  - "CABA" y "Ciudad Autonoma de Buenos Aires" son la misma provincia (el front manda CABA en el checkout).
//  - Costo fijo POR VENDEDOR: varios libros del mismo vendedor pagan un solo envio.
@Component
@RequiredArgsConstructor
public class EnvioPolicy {

    private static final Map<String, String> ALIAS = Map.of(
            "CABA", "CIUDAD AUTONOMA DE BUENOS AIRES",
            "CAPITAL FEDERAL", "CIUDAD AUTONOMA DE BUENOS AIRES");

    private final EnvioService envioService;

    // Envio que se cobra a un vendedor dentro de una compra
    public record EnvioVendedor(Long idVendedor, ZonaEnvio zona, double costo) {
    }

    public ZonaEnvio tipo(String provinciaVendedor, String provinciaComprador) {
        return determinarTipo(provinciaVendedor, provinciaComprador);
    }

    public static ZonaEnvio determinarTipo(String provinciaVendedor, String provinciaComprador) {
        String vendedor = normalizarONull(provinciaVendedor);
        String comprador = normalizarONull(provinciaComprador);
        return vendedor != null && vendedor.equals(comprador)
                ? ZonaEnvio.MISMA_PROVINCIA
                : ZonaEnvio.DISTINTA_PROVINCIA;
    }

    // Un envio por vendedor, en el orden en que aparecen los libros (como envioPorVendedor del front)
    public Map<Long, EnvioVendedor> enviosPorVendedor(Collection<Libro> libros, String provinciaComprador) {
        Map<ZonaEnvio, Double> tarifas = new EnumMap<>(ZonaEnvio.class);
        Map<Long, EnvioVendedor> porVendedor = new LinkedHashMap<>();
        for (Libro libro : libros) {
            Usuario vendedor = libro.getVendedor();
            if (porVendedor.containsKey(vendedor.getId())) {
                continue;
            }
            ZonaEnvio zona = determinarTipo(vendedor.getProvincia(), provinciaComprador);
            double costo = tarifas.computeIfAbsent(zona, envioService::getCostoPorZona);
            porVendedor.put(vendedor.getId(), new EnvioVendedor(vendedor.getId(), zona, costo));
        }
        return porVendedor;
    }

    public double costoPorVendedor(List<CarritoItem> items, String provinciaComprador) {
        double total = enviosPorVendedor(items.stream().map(CarritoItem::getLibro).toList(), provinciaComprador)
                .values().stream()
                .mapToDouble(EnvioVendedor::costo)
                .sum();
        return redondear(total);
    }

    // "  Córdoba " -> "CORDOBA"; "caba" -> "CIUDAD AUTONOMA DE BUENOS AIRES"; null o vacio -> "".
    // (se mantiene la firma que ya usaba LibroSpecification / LibroResponse)
    public static String normalizar(String provincia) {
        String n = normalizarONull(provincia);
        return n != null ? n : "";
    }

    // Igual que normalizar, pero null cuando no hay provincia (es lo que se guarda en Usuario.provinciaNormalizada)
    public static String normalizarONull(String provincia) {
        if (provincia == null) {
            return null;
        }
        String limpia = Normalizer.normalize(provincia, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[\\s\\u00A0]+", " ")
                .strip()
                .toUpperCase();
        if (limpia.isEmpty()) {
            return null;
        }
        return ALIAS.getOrDefault(limpia, limpia);
    }

    public static double redondear(double valor) {
        return Math.round(valor * 100) / 100.0;
    }
}