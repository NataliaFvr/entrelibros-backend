package com.uade.entrelibros.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
import com.uade.entrelibros.backend.exceptions.CodigoVerificacionInvalidoException;
import com.uade.entrelibros.backend.exceptions.EmailYaVerificadoException;
import com.uade.entrelibros.backend.exceptions.UsuarioDuplicadoException;
import com.uade.entrelibros.backend.exceptions.UsuarioNoEncontradoException;
import com.uade.entrelibros.backend.repository.CarritoItemRepository;
import com.uade.entrelibros.backend.repository.LibroRepository;
import com.uade.entrelibros.backend.repository.UsuarioRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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

    @Autowired
    private EmailService emailService;

    public Page<Usuario> getUsuarios(PageRequest pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Optional<Usuario> getUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId);
    }

    public Usuario createUsuario(String nombreUsuario, String email, String contrasena, String nombre,
            String apellido) {
        return createUsuario(nombreUsuario, email, contrasena, nombre, apellido, Rol.COMPRADOR);
    }

    public Usuario createUsuario(String nombreUsuario, String email, String contrasena, String nombre,
            String apellido, Rol rol) {
        List<Usuario> existentes = usuarioRepository.findByEmailOrNombreUsuario(email, nombreUsuario);
        if (!existentes.isEmpty())
            throw new UsuarioDuplicadoException();

        String contrasenaHasheada = passwordEncoder.encode(contrasena);
        String codigo = generarCodigo();

        Usuario nuevoUsuario = new Usuario(nombreUsuario, email, contrasenaHasheada, nombre, apellido, rol);
        nuevoUsuario.setEmailVerificado(false);
        nuevoUsuario.setCodigoVerificacion(codigo);
        nuevoUsuario.setCodigoVerificacionExpira(LocalDateTime.now().plusMinutes(15));

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        emailService.enviarCodigoVerificacion(guardado.getEmail(), guardado.getNombre(), codigo);

        return guardado;
    }

    @Override
    public Usuario updateUsuario(Long usuarioId, UsuarioUpdateRequest request) {
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
    public Usuario darDeBajaUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(UsuarioNoEncontradoException::new);

        if (usuario.getRol() == Rol.VENDEDOR) {
            darDeBajaLibrosYCarritos(usuario);
        }

        usuario.setEstado(EstadoUsuario.DADO_DE_BAJA);
        return usuarioRepository.save(usuario);
    }

    private void darDeBajaLibrosYCarritos(Usuario vendedor) {
        List<Libro> librosDelVendedor = libroRepository.findByVendedorId(vendedor.getId());
        for (Libro libro : librosDelVendedor) {
            libro.setEstadoPublicacion(EstadoPublicacion.DADA_DE_BAJA);
        }
        libroRepository.saveAll(librosDelVendedor);


    }

    public Usuario cambiarRol(Long usuarioId, Rol nuevoRol) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(UsuarioNoEncontradoException::new);
        usuario.setRol(nuevoRol);
        return usuarioRepository.save(usuario);
    }

    public Usuario reactivarUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(UsuarioNoEncontradoException::new);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario verificarEmail(String email, String codigo) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(UsuarioNoEncontradoException::new);

        if (Boolean.TRUE.equals(usuario.getEmailVerificado())) {
            throw new EmailYaVerificadoException();
        }

        boolean codigoValido = codigo != null
                && codigo.equals(usuario.getCodigoVerificacion())
                && usuario.getCodigoVerificacionExpira() != null
                && usuario.getCodigoVerificacionExpira().isAfter(LocalDateTime.now());

        if (!codigoValido) {
            throw new CodigoVerificacionInvalidoException();
        }

        usuario.setEmailVerificado(true);
        usuario.setCodigoVerificacion(null);
        usuario.setCodigoVerificacionExpira(null);

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario reenviarCodigoVerificacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(UsuarioNoEncontradoException::new);

        if (Boolean.TRUE.equals(usuario.getEmailVerificado())) {
            throw new EmailYaVerificadoException();
        }

        String codigo = generarCodigo();
        usuario.setCodigoVerificacion(codigo);
        usuario.setCodigoVerificacionExpira(LocalDateTime.now().plusMinutes(15));

        Usuario guardado = usuarioRepository.save(usuario);
        emailService.enviarCodigoVerificacion(guardado.getEmail(), guardado.getNombre(), codigo);

        return guardado;
    }

    private String generarCodigo() {
        int codigo = 100000 + new Random().nextInt(900000);
        return String.valueOf(codigo);
    }
    @Override
public Usuario solicitarResetPassword(String email) {
    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un usuario con ese email"));

    String codigo = generarCodigo();
    usuario.setCodigoResetPassword(codigo);
    usuario.setCodigoResetPasswordExpira(LocalDateTime.now().plusMinutes(15));

    Usuario guardado = usuarioRepository.save(usuario);
    emailService.enviarCodigoResetPassword(guardado.getEmail(), guardado.getNombre(), codigo);

    return guardado;
}

@Override
public Usuario cambiarContrasenia(String email, String codigo, String nuevaContrasenia) {
    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un usuario con ese email"));

    boolean codigoValido = codigo != null
            && codigo.equals(usuario.getCodigoResetPassword())
            && usuario.getCodigoResetPasswordExpira() != null
            && usuario.getCodigoResetPasswordExpira().isAfter(LocalDateTime.now());

    if (!codigoValido) {
        throw new CodigoVerificacionInvalidoException();
    }

    usuario.setContrasenaHash(passwordEncoder.encode(nuevaContrasenia));
    usuario.setCodigoResetPassword(null);
    usuario.setCodigoResetPasswordExpira(null);

    return usuarioRepository.save(usuario);
}
}