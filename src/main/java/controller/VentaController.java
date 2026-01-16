package controller;

import model.Venta;
import model.Producto;
import repository.VentaRepository;
import repository.ProductoRepository;

import java.util.List;

public class VentaController {
    private VentaRepository ventaRepository;
    private ProductoRepository productoRepository;
    
    public VentaController(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }
    
    public Venta registrarVenta(String usuario, List<Producto> productos, double total, String metodoPago) {
        try {
            // Validar stock antes de registrar la venta
            for (Producto productoCarrito : productos) {
                Producto productoBD = productoRepository.buscarProductoPorCodigo(productoCarrito.getCodigo());
                if (productoBD == null) {
                    throw new IllegalArgumentException("Producto no encontrado: " + productoCarrito.getCodigo());
                }
                if (productoBD.getStock() < productoCarrito.getStock()) {
                    throw new IllegalArgumentException("Stock insuficiente para: " + productoCarrito.getNombre() + 
                                                     ". Stock disponible: " + productoBD.getStock());
                }
            }
            
            // Crear la venta
            Venta venta = new Venta(usuario, productos, total, metodoPago);
            
            // Actualizar stock y registrar venta
            for (Producto productoCarrito : productos) {
                Producto productoBD = productoRepository.buscarProductoPorCodigo(productoCarrito.getCodigo());
                productoBD.setStock(productoBD.getStock() - productoCarrito.getStock());
                productoRepository.actualizarProducto(productoBD.getCodigo(), productoBD);
            }
            
            ventaRepository.registrarVenta(venta);
            return venta;
            
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar venta: " + e.getMessage(), e);
        }
    }
    
    public List<Venta> obtenerTodasVentas() {
        return ventaRepository.obtenerTodasVentas();
    }
    
    public List<Venta> obtenerVentasPorRangoFechas(String fechaInicio, String fechaFin) {
        return ventaRepository.obtenerVentasPorRangoFechas(fechaInicio, fechaFin);
    }
    
    public double calcularTotalVentas(List<Venta> ventas) {
        return ventas.stream().mapToDouble(Venta::getTotal).sum();
    }
}