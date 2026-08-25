package com.uade.entrelibros.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.entrelibros.backend.entity.Libro;

public interface LibroRepository extends JpaRepository<Libro, Long> {
}