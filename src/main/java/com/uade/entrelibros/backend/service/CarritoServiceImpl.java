package com.uade.entrelibros.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.entrelibros.backend.entity.Carrito;
import com.uade.entrelibros.backend.entity.CarritoItem;
import com.uade.entrelibros.backend.entity.Libro;
import com.uade.entrelibros.backend.entity.Orden;
import com.uade.entrelibros.backend.entity.OrdenItem;
import com.uade.entrelibros.backend.entity.OrdenVendedor;
import com.uade.entrelibros.backend.entity.Usuario;
import com.uade.entrelibros.backend.exceptions.CarritoVacioException;
import com.uade.entrelibros.backend.exceptions.ItemCarritoNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.LibroNoEncontradoException;
import com.uade.entrelibros.backend.exceptions.StockInsuficienteException;
import com.uade.entrelibros.backend.repository.CarritoItemRepository;
import com.uade.entrelibros.backend.repository.CarritoRepository;
import com.uade.entrelibros.backend.repository.LibroRepository;
import com.uade.entrelibros.backend.repository.OrdenItemRepository;
import com.uade.entrelibros.backend.repository.OrdenRepository;
import com.uade.entrelibros.backend.repository.OrdenVendedorRepository;
import com.uade.entrelibros.backend.repository.UsuarioRepository;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private CarritoItemRepository carritoItemRepository;
    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private OrdenRepository ordenRepository;
    @Autowired
    private OrdenVendedorRepository ordenVendedorRepository;
    @Autowired
    private OrdenItemRepository ordenItemRepository;

    public Carrito getOrCrearCarrito(Long idUsuario) {
        Carrito carrito = carritoRepository.findByUsuarioId(idUsuario);
        if (carrito != null) {
            return carrito;
        }
        // Nota: si el id de usuario no existe, esto tira un error genérico por ahora.
        // Cuando Persona 1 tenga una UsuarioNoEncontradoException, conviene usarla acá.
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return carritoRepository.save(new Carrito(usuario));
    }

    public List<CarritoItem> getItemsCarrito(Long idUsuario) {
        Carrito carrito = getOrCrearCarrito(idUsuario);
        return carritoItemRepository.findByCarritoId(carrito.getId());
    }

    public CarritoItem agregarItem(Long idUsuario, Long idLibro, Integer cantidad)
            throws LibroNoEncontradoException, StockInsuficienteException {

        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(LibroNoEncontradoException::new);

        if (libro.getStock() < cantidad) {
            throw new StockInsuficienteException();
        }

        Carrito carrito = getOrCrearCarrito(idUsuario);
        return carritoItemRepository.save(new CarritoItem(carrito, libro, cantidad));
    }

    public void quitarItem(Long idItem) throws ItemCarritoNoEncontradoException {
        CarritoItem item = carritoItemRepository.findById(idItem)
                .orElseThrow(ItemCarritoNoEncontradoException::new);
        carritoItemRepository.delete(item);
    }

    public Orden checkout(Long idUsuario, String provinciaDestino)
            throws CarritoVacioException, StockInsuficienteException {

        Carrito carrito = getOrCrearCarrito(idUsuario);
        List<CarritoItem> items = carritoItemRepository.findByCarritoId(carrito.getId());

        if (items.isEmpty()) {
            throw new CarritoVacioException();
        }

        // Revalidar stock: pudo haber cambiado desde que se agregó al carrito
        for (CarritoItem item : items) {
            if (item.getLibro().getStock() < item.getCantidad()) {
                throw new StockInsuficienteException();
            }
        }

        double subtotal = 0.0;
        for (CarritoItem item : items) {
            subtotal += precioConDescuento(item.getLibro()) * item.getCantidad();
        }

        // Por ahora total = subtotal (el costo de envío lo agrega Persona 4 con
        // Envio/EnvioItem)
        Orden orden = new Orden(carrito.getUsuario(), provinciaDestino, subtotal, subtotal);
        orden = ordenRepository.save(orden);

        Map<Long, OrdenVendedor> ordenVendedorPorVendedor = new HashMap<>();

        for (CarritoItem item : items) {
            Libro libro = item.getLibro();
            Usuario vendedor = libro.getVendedor();

            OrdenVendedor ordenVendedor = ordenVendedorPorVendedor.get(vendedor.getId());
            if (ordenVendedor == null) {
                ordenVendedor = ordenVendedorRepository.save(new OrdenVendedor(orden, vendedor));
                ordenVendedorPorVendedor.put(vendedor.getId(), ordenVendedor);
            }

            double precioUnitario = precioConDescuento(libro);
            ordenItemRepository.save(new OrdenItem(orden, libro, vendedor, item.getCantidad(), precioUnitario));

            libro.setStock(libro.getStock() - item.getCantidad());
            libroRepository.save(libro);

            carritoItemRepository.delete(item);
        }

        return orden;
    }

    private double precioConDescuento(Libro libro) {
        double descuento = libro.getDescuentoPct() != null ? libro.getDescuentoPct() : 0.0;
        return libro.getPrecio() * (1 - descuento / 100.0);
    }
}