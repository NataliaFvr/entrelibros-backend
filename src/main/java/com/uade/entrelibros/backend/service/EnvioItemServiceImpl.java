package com.uade.entrelibros.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Envio;
import com.uade.entrelibros.backend.entity.EnvioItem;
import com.uade.entrelibros.backend.entity.OrdenVendedor;
import com.uade.entrelibros.backend.entity.Rol;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.entity.ZonaEnvio;
import com.uade.entrelibros.backend.exceptions.EnvioItemNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.EnvioNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.OrdenVendedorNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.RolInvalidoException;
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
        List<EnvioItem> items = envioItemRepository.findAll();
        if (items.isEmpty()) {
            throw new ListaVaciaException("No hay envíos registrados");
        }
        return items;
    }

    public EnvioItem getEnvioItemById(Long idEnvioItem) throws EnvioItemNoEncontradoException {
        return envioItemRepository.findById(idEnvioItem)
                .orElseThrow(EnvioItemNoEncontradoException::new);
    }

    public List<EnvioItem> getEnvioItemsByOrdenVendedor(Long idOrdenVendedor) {
        List<EnvioItem> items = envioItemRepository.findByOrdenVendedorId(idOrdenVendedor);
        if (items.isEmpty()) {
            throw new ListaVaciaException("No hay envíos para esa orden de vendedor");
        }
        return items;
    }

    // El tipo de envio NO lo elige el vendedor: es el que se cobro en el checkout (OrdenVendedor.zonaEnvio) o,
    // en ordenes viejas, el que da EnvioPolicy (provincia del vendedor vs provincia destino). El campo "zona" del
    // request se ignora a proposito (queda en EnvioItemRequest para no romper clientes). Un solo envio por
    // OrdenVendedor: si ya existe se devuelve el mismo en vez de duplicar el cobro.
    public EnvioItem crearEnvioItem(Usuario vendedor, Long idOrdenVendedor, ZonaEnvio zonaIgnorada)
            throws OrdenVendedorNoEncontradaException, EnvioNoEncontradoException,
            AccionNoPermitidaException, RolInvalidoException {

        if (vendedor == null || vendedor.getRol() != Rol.VENDEDOR) {
            throw new RolInvalidoException();
        }

        OrdenVendedor ordenVendedor = ordenVendedorRepository.findById(idOrdenVendedor)
                .orElseThrow(OrdenVendedorNoEncontradaException::new);

        if (!ordenVendedor.getVendedor().getId().equals(vendedor.getId())) {
            throw new AccionNoPermitidaException();
        }

        List<EnvioItem> existentes = envioItemRepository.findByOrdenVendedorId(idOrdenVendedor);
        if (!existentes.isEmpty()) {
            return existentes.get(0);
        }

        ZonaEnvio zona = ordenVendedor.getZonaEnvio() != null
                ? ordenVendedor.getZonaEnvio()
                : EnvioPolicy.determinarTipo(ordenVendedor.getVendedor().getProvincia(),
                        ordenVendedor.getOrden().getProvinciaDestino());

        Envio envio = envioRepository.findByZona(zona);
        if (envio == null)
            throw new EnvioNoEncontradoException();

        double costo = ordenVendedor.getCostoEnvio() != null ? ordenVendedor.getCostoEnvio() : envio.getCostoFijo();
        return envioItemRepository.save(new EnvioItem(ordenVendedor, envio, costo));
    }
}