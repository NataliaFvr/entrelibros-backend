package com.uade.entrelibros.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.dto.CargoServicioRequest;
import com.uade.entrelibros.backend.entity.dto.CargoServicioResponse;
import com.uade.entrelibros.backend.service.CargoServicioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("configuracion/cargo-servicio")
@PreAuthorize("hasAuthority('ADMIN')")
public class CargoServicioController {

    @Autowired
    private CargoServicioService cargoServicioService;

    @GetMapping
    public ResponseEntity<CargoServicioResponse> obtener() {
        return ResponseEntity.ok(CargoServicioResponse.from(cargoServicioService.obtener()));
    }

    @PatchMapping
    public ResponseEntity<CargoServicioResponse> actualizar(@Valid @RequestBody CargoServicioRequest request) {
        return ResponseEntity.ok(CargoServicioResponse.from(cargoServicioService.actualizar(request.getPorcentaje())));
    }
}