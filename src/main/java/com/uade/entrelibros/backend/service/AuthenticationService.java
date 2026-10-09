package com.uade.entrelibros.backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.config.JwtService;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.AuthenticationRequest;
import com.uade.entrelibros.backend.entity.dto.AuthenticationResponse;
import com.uade.entrelibros.backend.entity.dto.RefreshTokenRequest;
import com.uade.entrelibros.backend.entity.dto.UsuarioRequest;
import com.uade.entrelibros.backend.entity.dto.VerificarEmailRequest;
import com.uade.entrelibros.backend.exceptions.RefreshTokenInvalidoException;
import com.uade.entrelibros.backend.exceptions.UsuarioDuplicadoException;
import com.uade.entrelibros.backend.exceptions.UsuarioNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.CuentaDadaDeBajaException;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.JwtException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Registro publico: SIEMPRE crea un COMPRADOR. El campo "rol" del body se ignora a proposito
    // (queda en UsuarioRequest porque POST /usuarios, solo ADMIN, si lo usa).
    // El unico camino a VENDEDOR es POST /usuarios/solicitud-vendedor + aprobacion de un admin.
    public void register(UsuarioRequest request) throws UsuarioDuplicadoException {
        usuarioService.createUsuario(
                request.getNombreUsuario(),
                request.getEmail(),
                request.getContrasena(),
                request.getNombre(),
                request.getApellido(),
                Rol.COMPRADOR);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(UsuarioNoEncontradoException::new);
        if (usuario.getEstado() == com.uade.entrelibros.backend.entity.EstadoUsuario.DADO_DE_BAJA) {
            throw new CuentaDadaDeBajaException();
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getContrasena()));

        String jwtToken = jwtService.generateToken(usuario);
        return buildResponse(usuario, jwtToken);
    }

    public AuthenticationResponse refresh(RefreshTokenRequest request) {
        try {
            String email = jwtService.extractUsername(request.getRefreshToken());
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(RefreshTokenInvalidoException::new);

            if (!jwtService.isRefreshTokenValid(request.getRefreshToken(), usuario)) {
                throw new RefreshTokenInvalidoException();
            }

            return buildResponse(usuario, jwtService.generateToken(usuario));
        } catch (JwtException | IllegalArgumentException exception) {
            throw new RefreshTokenInvalidoException();
        }
    }

    public AuthenticationResponse verificarEmail(VerificarEmailRequest request) {
        Usuario usuario = usuarioService.verificarEmail(request.getEmail(), request.getCodigo());
        String jwtToken = jwtService.generateToken(usuario);
        return buildResponse(usuario, jwtToken);
    }

    private AuthenticationResponse buildResponse(Usuario usuario, String jwtToken) {
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(jwtService.generateRefreshToken(usuario))
                .usuarioId(usuario.getId())
                .nombreUsuario(usuario.getNombreUsuario())
                .rol(usuario.getRol().name())
                .build();
    }
}