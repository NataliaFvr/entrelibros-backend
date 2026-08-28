package com.uade.entrelibros.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.Envio;
import com.uade.entrelibros.backend.entity.ZonaEnvio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {

    @Query(value = "select e from Envio e where e.zona = ?1")
    Envio findByZona(ZonaEnvio zona);
}
