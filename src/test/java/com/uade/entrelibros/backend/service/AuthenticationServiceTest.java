package com.uade.entrelibros.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import com.uade.entrelibros.backend.config.JwtService;
import com.uade.entrelibros.backend.entity.EstadoUsuario;
import com.uade.entrelibros.backend.entity.RefreshToken;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.AuthenticationResponse;
import com.uade.entrelibros.backend.entity.dto.RefreshTokenRequest;
import com.uade.entrelibros.backend.entity.dto.UsuarioRequest;
import com.uade.entrelibros.backend.exceptions.RefreshTokenInvalidoException;
import com.uade.entrelibros.backend.repository.UsuarioRepository;
import com.uade.entrelibros.backend.repository.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UsuarioRequest request(Rol rol) {
        UsuarioRequest r = new UsuarioRequest();
        r.setNombreUsuario("ana");
        r.setEmail("ana@mail.com");
        r.setContrasena("Clave#123");
        r.setNombre("Ana");
        r.setApellido("Gil");
        r.setRol(rol);
        return r;
    }

    @ParameterizedTest
    @EnumSource(Rol.class)
    void registerSiempreCreaCompradorIgnorandoElRolDelBody(Rol rolPedido) {
        authenticationService.register(request(rolPedido));

        verify(usuarioService).createUsuario("ana", "ana@mail.com", "Clave#123", "Ana", "Gil", Rol.COMPRADOR);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void registerSinRolCreaComprador() {
        authenticationService.register(request(null));

        verify(usuarioService).createUsuario("ana", "ana@mail.com", "Clave#123", "Ana", "Gil", Rol.COMPRADOR);
        verifyNoMoreInteractions(usuarioService);
    }

    private Usuario ana() {
        Usuario u = new Usuario("ana", "ana@mail.com", "hash", "Ana", "Gil");
        u.setId(1L);
        return u;
    }

    private RefreshTokenRequest pedido(String token) {
        RefreshTokenRequest r = new RefreshTokenRequest();
        r.setRefreshToken(token);
        return r;
    }

    private RefreshToken guardado(Usuario usuario, boolean revocado) {
        RefreshToken t = new RefreshToken(usuario, "id-viejo", Instant.now().plusSeconds(3600));
        t.setRevocado(revocado);
        return t;
    }

    @Test
    void refreshRevocaElTokenUsadoYEntregaUnoNuevo() {
        Usuario usuario = ana();
        RefreshToken viejo = guardado(usuario, false);
        when(jwtService.extractUsername("viejo")).thenReturn("ana@mail.com");
        when(jwtService.extractTokenId("viejo")).thenReturn("id-viejo");
        when(usuarioRepository.findByEmail("ana@mail.com")).thenReturn(Optional.of(usuario));
        when(jwtService.isRefreshTokenValid("viejo", usuario)).thenReturn(true);
        when(refreshTokenRepository.findByTokenId("id-viejo")).thenReturn(Optional.of(viejo));
        when(jwtService.generateToken(usuario)).thenReturn("access");
        when(jwtService.generateRefreshToken(usuario)).thenReturn("nuevo");
        when(jwtService.extractExpiration("nuevo")).thenReturn(new Date(System.currentTimeMillis() + 3600_000));
        when(jwtService.extractTokenId("nuevo")).thenReturn("id-nuevo");

        AuthenticationResponse respuesta = authenticationService.refresh(pedido("viejo"));

        assertTrue(viejo.getRevocado());
        assertEquals("nuevo", respuesta.getRefreshToken());
        verify(refreshTokenRepository).save(argThat(t -> "id-nuevo".equals(t.getTokenId()) && !t.getRevocado()));
    }

    @Test
    void refreshConTokenRevocadoFalla() {
        Usuario usuario = ana();
        when(jwtService.extractUsername("viejo")).thenReturn("ana@mail.com");
        when(jwtService.extractTokenId("viejo")).thenReturn("id-viejo");
        when(usuarioRepository.findByEmail("ana@mail.com")).thenReturn(Optional.of(usuario));
        when(jwtService.isRefreshTokenValid("viejo", usuario)).thenReturn(true);
        when(refreshTokenRepository.findByTokenId("id-viejo")).thenReturn(Optional.of(guardado(usuario, true)));

        assertThrows(RefreshTokenInvalidoException.class, () -> authenticationService.refresh(pedido("viejo")));
        verify(jwtService, never()).generateToken(usuario);
    }

    @Test
    void logoutRevocaAunqueElUsuarioEsteDadoDeBaja() {
        Usuario usuario = ana();
        usuario.setEstado(EstadoUsuario.DADO_DE_BAJA);
        RefreshToken token = guardado(usuario, false);
        when(jwtService.extractUsername("viejo")).thenReturn("ana@mail.com");
        when(jwtService.extractTokenId("viejo")).thenReturn("id-viejo");
        when(refreshTokenRepository.findByTokenId("id-viejo")).thenReturn(Optional.of(token));

        authenticationService.logout(pedido("viejo"));

        assertTrue(token.getRevocado());
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void logoutConTokenDeOtroUsuarioFalla() {
        when(jwtService.extractUsername("viejo")).thenReturn("otro@mail.com");
        when(jwtService.extractTokenId("viejo")).thenReturn("id-viejo");
        when(refreshTokenRepository.findByTokenId("id-viejo")).thenReturn(Optional.of(guardado(ana(), false)));

        assertThrows(RefreshTokenInvalidoException.class, () -> authenticationService.logout(pedido("viejo")));
        verify(refreshTokenRepository, never()).save(any());
    }
}