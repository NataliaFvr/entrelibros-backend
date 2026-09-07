package com.uade.entrelibros.backend.service;

import java.util.List;

import com.uade.entrelibros.backend.entity.EnvioItem;
import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.Pago;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoPagableException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;

public interface PagoService {

    List<Pago> getPagos();

    Pago getPagoById(Usuario comprador, Long idPago);

    List<Pago> getPagosByOrden(Usuario comprador, Long idOrden);

    Pago crearPago(Usuario comprador, Long idOrden, String proveedor);

    List<OrdenItem> getItemsDeOrdenPagada(Long idOrden);
<<<<<<< HEAD
}
=======

    List<EnvioItem> getEnvioItemsDeOrdenPagada(Long idOrden);
}
>>>>>>> 2265f6276d9c8b01ecdf2c1c0825b9275fd72f2f
