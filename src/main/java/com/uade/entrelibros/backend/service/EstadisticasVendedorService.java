package com.uade.entrelibros.backend.service;

import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.dto.EstadisticasVendedorResponse;

public interface EstadisticasVendedorService {

    EstadisticasVendedorResponse getEstadisticas(Usuario vendedor);
}
