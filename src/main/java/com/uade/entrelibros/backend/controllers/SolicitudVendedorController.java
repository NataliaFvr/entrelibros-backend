package com.uade.entrelibros.backend.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.entrelibros.backend.entity.EstadoSolicitud;
import com.uade.entrelibros.backend.entity.SolicitudVendedor;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.ResolverSolicitudRequest;
import com.uade.entrelibros.backend.entity.dto.SolicitudVendedorRequest;
import com.uade.entrelibros.backend.entity.dto.SolicitudVendedorResponse;
import com.uade.entrelibros.backend.service.SolicitudVendedorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("solicitudes-vendedor")
public class SolicitudVendedorController {

    @Autowired
    private SolicitudVendedorService solicitudVendedorService;

    @PreAuthorize("hasAuthority('COMPRADOR')")
    @PostMapping
    public ResponseEntity<SolicitudVendedorResponse> crear(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody SolicitudVendedorRequest request) {
        SolicitudVendedor result = solicitudVendedorService.crear(usuario, request.getMotivo());
        return ResponseEntity.created(URI.create("/solicitudes-vendedor/" + result.getId()))
                .body(SolicitudVendedorResponse.from(result));
    }


    @GetMapping("/mias")
    public ResponseEntity<Page<SolicitudVendedorResponse>> getMisSolicitudes(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<SolicitudVendedorResponse> resultado = solicitudVendedorService
                .getMisSolicitudes(usuario.getId(), PageRequest.of(page, size))
                .map(SolicitudVendedorResponse::from);
        return ResponseEntity.ok(resultado);
    }
   
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<SolicitudVendedorResponse>> getSolicitudes(
            @RequestParam(defaultValue = "PENDIENTE") EstadoSolicitud estado,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<SolicitudVendedorResponse> resultado = solicitudVendedorService
                .getSolicitudesPorEstado(estado, PageRequest.of(page, size))
                .map(SolicitudVendedorResponse::from);
        return ResponseEntity.ok(resultado);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{idSolicitud}/resolucion")
    public ResponseEntity<SolicitudVendedorResponse> resolver(
            @PathVariable Long idSolicitud,
            @AuthenticationPrincipal Usuario admin,
            @Valid @RequestBody ResolverSolicitudRequest request) {
        SolicitudVendedor result = solicitudVendedorService.resolver(
                idSolicitud, request.getEstado(), request.getComentario(), admin);
        return ResponseEntity.ok(SolicitudVendedorResponse.from(result));
    }
}