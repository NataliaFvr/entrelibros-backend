package com.uade.entrelibros.backend.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import com.uade.entrelibros.backend.config.JwtService;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.dto.UsuarioRequest;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
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
}