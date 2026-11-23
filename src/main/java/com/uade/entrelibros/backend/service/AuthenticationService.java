package com.uade.entrelibros.backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.config.JwtService;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.RefreshToken;
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
import com.uade.entrelibros.backend.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.JwtException;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
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

    @Transactional
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

    @Transactional
    public AuthenticationResponse refresh(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            String email = jwtService.extractUsername(refreshToken);
            String tokenId = jwtService.extractTokenId(refreshToken);
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(RefreshTokenInvalidoException::new);

            if (!jwtService.isRefreshTokenValid(refreshToken, usuario)) {
                throw new RefreshTokenInvalidoException();
            }

            RefreshToken almacenado = refreshTokenRepository.findByTokenId(tokenId)
                    .orElseThrow(RefreshTokenInvalidoException::new);
            if (Boolean.TRUE.equals(almacenado.getRevocado())
                    || !almacenado.getUsuario().getId().equals(usuario.getId())
                    || almacenado.getExpiresAt().isBefore(Instant.now())) {
                throw new RefreshTokenInvalidoException();
            }

            almacenado.setRevocado(true);
            refreshTokenRepository.save(almacenado);

            return buildResponse(usuario, jwtService.generateToken(usuario));
        } catch (JwtException | IllegalArgumentException exception) {
            throw new RefreshTokenInvalidoException();
        }
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            String email = jwtService.extractUsername(refreshToken);
            String tokenId = jwtService.extractTokenId(refreshToken);
            // No exige usuario activo: un usuario dado de baja también tiene que poder revocar su token
            RefreshToken almacenado = refreshTokenRepository.findByTokenId(tokenId)
                    .orElseThrow(RefreshTokenInvalidoException::new);
            if (!almacenado.getUsuario().getEmail().equals(email)) {
                throw new RefreshTokenInvalidoException();
            }
            almacenado.setRevocado(true);
            refreshTokenRepository.save(almacenado);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new RefreshTokenInvalidoException();
        }
    }

    @Transactional
    public AuthenticationResponse verificarEmail(VerificarEmailRequest request) {
        Usuario usuario = usuarioService.verificarEmail(request.getEmail(), request.getCodigo());
        String jwtToken = jwtService.generateToken(usuario);
        return buildResponse(usuario, jwtToken);
    }

    private AuthenticationResponse buildResponse(Usuario usuario, String jwtToken) {
        String refreshToken = jwtService.generateRefreshToken(usuario);
        Date expiresAt = jwtService.extractExpiration(refreshToken);
        String tokenId = jwtService.extractTokenId(refreshToken);
        refreshTokenRepository.eliminarExpirados(Instant.now());
        refreshTokenRepository.save(new RefreshToken(usuario, tokenId, expiresAt.toInstant()));
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .usuarioId(usuario.getId())
                .nombreUsuario(usuario.getNombreUsuario())
                .rol(usuario.getRol().name())
                .build();
    }
}