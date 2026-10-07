package com.uade.entrelibros.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.VendedorPerfilResponse;
import com.uade.entrelibros.backend.exceptions.VendedorNoEncontradoException;
import com.uade.entrelibros.backend.repository.ResenaVendedorRepository;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

@Service
public class VendedorPerfilService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ResenaVendedorRepository resenaVendedorRepository;

    public VendedorPerfilResponse getPerfil(Long idVendedor) {
        Usuario vendedor = usuarioRepository.findById(idVendedor)
                .filter(usuario -> usuario.getRol() == Rol.VENDEDOR)
                .orElseThrow(VendedorNoEncontradoException::new);
        return VendedorPerfilResponse.from(
                vendedor,
                resenaVendedorRepository.promedioPorVendedor(idVendedor),
                resenaVendedorRepository.cantidadPorVendedor(idVendedor));
    }
}
