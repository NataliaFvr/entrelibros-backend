package com.uade.entrelibros.backend.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.entrelibros.backend.entity.EstadoPago;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.Pago;
import com.uade.entrelibros.backend.entity.TipoNotificacion;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.AccionNoPermitidaException;
import com.uade.entrelibros.backend.exceptions.ListaVaciaException;
import com.uade.entrelibros.backend.exceptions.OrdenNoEncontradaException;
import com.uade.entrelibros.backend.exceptions.PagoNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.OrdenNoPagableException;
import com.uade.entrelibros.backend.repository.OrdenItemRepository;
import com.uade.entrelibros.backend.repository.OrdenRepository;
import com.uade.entrelibros.backend.repository.PagoRepository;

@Service
public class PagoServiceImpl implements PagoService {

    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private OrdenRepository ordenRepository;
    @Autowired
    private OrdenService ordenService;
    @Autowired
    private NotificacionService notificacionService;
    @Autowired
    private OrdenItemRepository ordenItemRepository;

    public List<Pago> getPagos() {
        List<Pago> pagos = pagoRepository.findAll();
        if (pagos.isEmpty()) {
            throw new ListaVaciaException("No hay pagos registrados");
        }
        return pagos;
    }
    public Pago getPagoById(Usuario comprador, Long idPago) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(PagoNoEncontradoException::new);
        validarComprador(pago.getOrden(), comprador);
        return pago;
    }

    public List<Pago> getPagosByOrden(Usuario comprador, Long idOrden) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(OrdenNoEncontradaException::new);
        validarComprador(orden, comprador);
        List<Pago> pagos = pagoRepository.findByOrdenId(idOrden);
        if (pagos.isEmpty()) {
            throw new ListaVaciaException("Esa orden no tiene pagos registrados");
        }
        return pagos;
    }

    @Transactional
    public Pago crearPago(Usuario comprador, Long idOrden, String proveedor) {
        if (ordenService.liberarReservaVencida(idOrden)) {
            throw new OrdenNoPagableException();
        }

        Orden orden = ordenRepository.findByIdConCandado(idOrden)
                .orElseThrow(OrdenNoEncontradaException::new);

        validarComprador(orden, comprador);
        if (orden.getEstadoPago() != EstadoPago.PENDIENTE) {
            throw new OrdenNoPagableException();
        }

        // Pago simulado: se aprueba automaticamente y se refleja el estado en la orden
        Pago pago = new Pago(orden, proveedor);
        pago = pagoRepository.save(pago);

        orden.setEstadoPago(pago.getResultado());
        ordenRepository.save(orden);

        // Misma transaccion que el pago: si algo falla, no queda una notificacion de un pago que no existe
        if (pago.getResultado() == EstadoPago.SIMULADO_APROBADO) {
            notificarCompraYPago(orden);
            notificarVendedores(orden);
        }

        return pago;
    }

    // Una notificacion por vendedor (no por item): una orden puede tener varios libros del mismo vendedor
    private void notificarVendedores(Orden orden) {
        Map<Long, List<OrdenItem>> itemsPorVendedor = new LinkedHashMap<>();
        for (OrdenItem item : ordenItemRepository.findByOrdenId(orden.getId())) {
            itemsPorVendedor.computeIfAbsent(item.getVendedor().getId(), id -> new java.util.ArrayList<>()).add(item);
        }

        for (List<OrdenItem> items : itemsPorVendedor.values()) {
            int unidades = 0;
            double monto = 0.0;
            for (OrdenItem item : items) {
                unidades += item.getCantidad();
                monto += item.getCantidad() * item.getPrecioUnitario();
            }
            notificacionService.crearDeOrden(items.get(0).getVendedor(), TipoNotificacion.VENTA_REALIZADA,
                    String.format(Locale.forLanguageTag("es-AR"),
                            "Nueva venta en la compra #%d: %d unidad(es) por $%,.2f",
                            orden.getId(), unidades, monto),
                    orden);
        }
    }

    // El front muestra las dos ("Compra confirmada" y "Pago procesado"), asi que se generan las dos
    private void notificarCompraYPago(Orden orden) {
        Usuario comprador = orden.getComprador();
        notificacionService.crearDeOrden(comprador, TipoNotificacion.COMPRA_CONFIRMADA,
                "Tu compra #" + orden.getId() + " fue confirmada", orden);
        notificacionService.crearDeOrden(comprador, TipoNotificacion.PAGO_PROCESADO,
                String.format(Locale.forLanguageTag("es-AR"),
                        "Se proceso el pago de $%,.2f de tu compra #%d", orden.getTotal(), orden.getId()),
                orden);
    }

    private void validarComprador(Orden orden, Usuario comprador) {
        if (orden == null || orden.getComprador() == null || comprador == null
                || !orden.getComprador().getId().equals(comprador.getId())) {
            throw new AccionNoPermitidaException();
        }
    }
}