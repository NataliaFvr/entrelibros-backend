package com.uade.entrelibros.backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.config.JwtService;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.AuthenticationRequest;
import com.uade.entrelibros.backend.entity.dto.AuthenticationResponse;
import com.uade.entrelibros.backend.entity.dto.UsuarioRequest;
import com.uade.entrelibros.backend.exceptions.UsuarioDuplicadoException;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(UsuarioRequest request) throws UsuarioDuplicadoException {
    Rol rolSolicitado = request.getRol();
    Rol rolFinal = (rolSolicitado == null || rolSolicitado == Rol.ADMIN)
            ? Rol.COMPRADOR
            : rolSolicitado;

    Usuario usuario = usuarioService.createUsuario(
            request.getNombreUsuario(),
            request.getEmail(),
            request.getContrasena(),
            request.getNombre(),
            request.getApellido(),
            rolFinal);      

    String jwtToken = jwtService.generateToken(usuario);
    return buildResponse(usuario, jwtToken);
}

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getContrasena()));

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(usuario);
        return buildResponse(usuario, jwtToken);
    }

    private AuthenticationResponse buildResponse(Usuario usuario, String jwtToken) {
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .usuarioId(usuario.getId())
                .nombreUsuario(usuario.getNombreUsuario())
                .rol(usuario.getRol().name())
                .build();
    }
}