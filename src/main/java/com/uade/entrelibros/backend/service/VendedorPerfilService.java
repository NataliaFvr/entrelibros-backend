package com.uade.entrelibros.backend.service;

import com.uade.entrelibros.backend.entity.dto.VendedorPerfilResponse;

public interface VendedorPerfilService {

    VendedorPerfilResponse getPerfil(Long idVendedor);
}
