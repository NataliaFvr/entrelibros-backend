package com.uade.entrelibros.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.entrelibros.backend.entity.Categoria;
import com.uade.entrelibros.backend.exceptions.CategoriaDuplicadaException;
import com.uade.entrelibros.backend.repository.CategoriaRepository;

@RestController
@RequestMapping("/test-rollback")
public class RollbackTestController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<String> probarRollback() {
        // 1) Escribe en la base DENTRO de la transaccion
        categoriaRepository.save(new Categoria("ROLLBACK_TEST_" + System.currentTimeMillis()));

        // 2) Excepcion de negocio DESPUES del save.
        // Si el rollback funciona, ese save de arriba nunca tiene que llegar a la base.
        throw new CategoriaDuplicadaException();
    }
}