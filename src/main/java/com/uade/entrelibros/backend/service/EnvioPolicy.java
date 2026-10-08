package com.uade.entrelibros.backend.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.uade.entrelibros.backend.entity.CarritoItem;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.ZonaEnvio;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EnvioPolicy {

    private final EnvioService envioService;

    public ZonaEnvio tipo(String provinciaVendedor, String provinciaComprador) {
        return determinarTipo(provinciaVendedor, provinciaComprador);
    }

    public static ZonaEnvio determinarTipo(String provinciaVendedor, String provinciaComprador) {
        if (provinciaVendedor == null || provinciaComprador == null
                || provinciaVendedor.isBlank() || provinciaComprador.isBlank()) {
            return ZonaEnvio.DISTINTA_PROVINCIA;
        }
        return normalizar(provinciaVendedor).equals(normalizar(provinciaComprador))
                ? ZonaEnvio.MISMA_PROVINCIA
                : ZonaEnvio.DISTINTA_PROVINCIA;
    }

    public double costoPorVendedor(List<CarritoItem> items, String provinciaComprador) {
        Map<Long, Usuario> vendedores = items.stream()
                .map(CarritoItem::getLibro)
                .map(Libro::getVendedor)
                .collect(Collectors.toMap(Usuario::getId, Function.identity(), (a, b) -> a));
        return vendedores.values().stream()
                .mapToDouble(vendedor -> envioService.getCostoPorZona(
                        tipo(vendedor.getProvincia(), provinciaComprador)))
                .sum();
    }

    public static String normalizar(String provincia) {
        if (provincia == null) {
            return "";
        }
        return Normalizer.normalize(provincia.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("\\s+", " ")
                .toUpperCase();
    }
}
