package com.uade.entrelibros.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.ConfiguracionCargo;

@Repository
public interface ConfiguracionCargoRepository extends JpaRepository<ConfiguracionCargo, Long> {

    Optional<ConfiguracionCargo> findFirstByOrderByIdAsc();
}