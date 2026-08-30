package com.uade.entrelibros.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.EstadoPublicacion;
import com.uade.entrelibros.backend.entity.EstadoUsuario;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.UsuarioUpdateRequest;
import com.uade.entrelibros.backend.exceptions.UsuarioDuplicadoException;
import com.uade.entrelibros.backend.exceptions.UsuarioNoEncontradoException;
import com.uade.entrelibros.backend.repository.CarritoItemRepository;
import com.uade.entrelibros.backend.repository.LibroRepository;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    public Page<Usuario> getUsuarios(PageRequest pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Optional<Usuario> getUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId);
    }

    public Usuario createUsuario(String nombreUsuario, String email, String contrasena, String nombre,
            String apellido) throws UsuarioDuplicadoException {
        return createUsuario(nombreUsuario, email, contrasena, nombre, apellido, Rol.COMPRADOR);
    }

    public Usuario createUsuario(String nombreUsuario, String email, String contrasena, String nombre,
            String apellido, Rol rol) throws UsuarioDuplicadoException {
        List<Usuario> existentes = usuarioRepository.findByEmailOrNombreUsuario(email, nombreUsuario);
        if (!existentes.isEmpty())
            throw new UsuarioDuplicadoException();

        String contrasenaHasheada = passwordEncoder.encode(contrasena);
        return usuarioRepository.save(
                new Usuario(nombreUsuario, email, contrasenaHasheada, nombre, apellido, rol));
    }

    @Override
    public Usuario updateUsuario(Long usuarioId, UsuarioUpdateRequest request)
            throws UsuarioDuplicadoException, UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(UsuarioNoEncontradoException::new);

        boolean cambiaEmail = request.getEmail() != null && !request.getEmail().equals(usuario.getEmail());
        boolean cambiaNombreUsuario = request.getNombreUsuario() != null
                && !request.getNombreUsuario().equals(usuario.getNombreUsuario());

        if (cambiaEmail || cambiaNombreUsuario) {
            List<Usuario> existentes = usuarioRepository.findByEmailOrNombreUsuario(
                    cambiaEmail ? request.getEmail() : usuario.getEmail(),
                    cambiaNombreUsuario ? request.getNombreUsuario() : usuario.getNombreUsuario());

            boolean conflicto = existentes.stream().anyMatch(u -> !u.getId().equals(usuarioId));
            if (conflicto)
                throw new UsuarioDuplicadoException();
        }

        if (request.getNombreUsuario() != null)
            usuario.setNombreUsuario(request.getNombreUsuario());
        if (request.getEmail() != null)
            usuario.setEmail(request.getEmail());
        if (request.getNombre() != null)
            usuario.setNombre(request.getNombre());
        if (request.getApellido() != null)
            usuario.setApellido(request.getApellido());
        if (request.getContrasena() != null && !request.getContrasena().isBlank())
            usuario.setContrasenaHash(passwordEncoder.encode(request.getContrasena()));

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long usuarioId) throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(UsuarioNoEncontradoException::new);

        if (usuario.getRol() == Rol.VENDEDOR) {
            darDeBajaLibrosYCarritos(usuario);
        }

        usuario.setEstado(EstadoUsuario.DADO_DE_BAJA);
        usuarioRepository.save(usuario);
    }

    private void darDeBajaLibrosYCarritos(Usuario vendedor) {
        List<Libro> librosDelVendedor = libroRepository.findByVendedorId(vendedor.getId());
        for (Libro libro : librosDelVendedor) {
            libro.setEstadoPublicacion(EstadoPublicacion.DADA_DE_BAJA);
        }
        libroRepository.saveAll(librosDelVendedor);

        carritoItemRepository.deleteAll(
                carritoItemRepository.findByVendedorId(vendedor.getId()));
    }
}