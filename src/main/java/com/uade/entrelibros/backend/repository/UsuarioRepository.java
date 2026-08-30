package com.uade.entrelibros.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.entrelibros.backend.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "select u from Usuario u where u.email = ?1 or u.nombreUsuario = ?2")
    List<Usuario> findByEmailOrNombreUsuario(String email, String nombreUsuario);

    Optional<Usuario> findByEmail(String email);
}