package com.uade.entrelibros.backend.config;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.repository.UsuarioRepository;
import com.uade.entrelibros.backend.service.EnvioPolicy;

// Completa Usuario.provinciaNormalizada en las filas que existian antes de este cambio (ddl-auto=update crea la
// columna vacia). Se hace en Java para usar EXACTAMENTE EnvioPolicy.normalizarONull. Idempotente: solo guarda
// los usuarios cuyo valor no coincide.
@Component
public class ProvinciaNormalizadaBackfill implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ProvinciaNormalizadaBackfill.class);

    private final UsuarioRepository usuarioRepository;

    public ProvinciaNormalizadaBackfill(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Usuario> desactualizados = usuarioRepository.findAll().stream()
                .filter(u -> !Objects.equals(u.getProvinciaNormalizada(),
                        EnvioPolicy.normalizarONull(u.getProvincia())))
                .toList();
        if (desactualizados.isEmpty()) {
            return;
        }
        desactualizados.forEach(u -> u.setProvincia(u.getProvincia()));
        usuarioRepository.saveAll(desactualizados);
        log.info("provinciaNormalizada actualizada en {} usuarios", desactualizados.size());
    }
}