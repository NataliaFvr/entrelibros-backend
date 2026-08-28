package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Envio;
import com.uade.entrelibros.backend.entity.EnvioItem;
import com.uade.entrelibros.backend.entity.OrdenVendedor;
import com.uade.entrelibros.backend.entity.ZonaEnvio;
import com.uade.entrelibros.backend.exceptions.EnvioItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.EnvioNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.OrdenVendedorNoEncontradaException;
import com.uade.entrelibros.backend.repository.EnvioItemRepository;
import com.uade.entrelibros.backend.repository.EnvioRepository;
import com.uade.entrelibros.backend.repository.OrdenVendedorRepository;

@Service
public class EnvioItemServiceImpl implements EnvioItemService {

    @Autowired
    private EnvioItemRepository envioItemRepository;
    @Autowired
    private EnvioRepository envioRepository;
    @Autowired
    private OrdenVendedorRepository ordenVendedorRepository;

    public List<EnvioItem> getEnvioItems() {
        return envioItemRepository.findAll();
    }

    public EnvioItem getEnvioItemById(Long idEnvioItem) throws EnvioItemNoEncontradoException {
        return envioItemRepository.findById(idEnvioItem)
                .orElseThrow(EnvioItemNoEncontradoException::new);
    }

    public List<EnvioItem> getEnvioItemsByOrdenVendedor(Long idOrdenVendedor) {
        return envioItemRepository.findByOrdenVendedorId(idOrdenVendedor);
    }

    public EnvioItem crearEnvioItem(Long idOrdenVendedor, ZonaEnvio zona)
            throws OrdenVendedorNoEncontradaException, EnvioNoEncontradoException {

        OrdenVendedor ordenVendedor = ordenVendedorRepository.findById(idOrdenVendedor)
                .orElseThrow(OrdenVendedorNoEncontradaException::new);

        // La tarifa aplicada sale del catalogo de envios segun la zona de destino
        Envio envio = envioRepository.findByZona(zona);
        if (envio == null)
            throw new EnvioNoEncontradoException();

        return envioItemRepository.save(new EnvioItem(ordenVendedor, envio, envio.getCostoFijo()));
    }
}
