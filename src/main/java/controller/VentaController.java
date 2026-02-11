package controller;

import model.Venta;
import model.Producto;
import repository.VentaRepository;
import repository.ProductoRepository;
import config.CedulaValidator;

import java.util.List;

public class VentaController {
    private VentaRepository ventaRepository;
    private ProductoRepository productoRepository;
    
    public VentaController(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }
    
    public Venta registrarVenta(String usuario, List<Producto> productos, double total, String metodoPago) {
        // Registrar sin cédula (para compatibilidad backwards)
        return registrarVentaConCedula(usuario, "", productos, total, metodoPago);
    }
    
    /**
     * HU-08: Registra una venta con validación de cédula del cliente
     * @param usuario Username del cajero
     * @param cedulaCliente Cédula del cliente (validada)
     * @param productos Lista de productos a vender
     * @param total Total de la venta
     * @param metodoPago Método de pago (Efectivo, Tarjeta, etc.)
     * @return Venta registrada
     * @throws IllegalArgumentException Si la cédula no es válida o stock insuficiente
     */
    public Venta registrarVentaConCedula(String usuario, String cedulaCliente, List<Producto> productos, 
                                         double total, String metodoPago) {
        try {
            // VALIDACIÓN HU-08: Validar cédula si se proporciona
            if (cedulaCliente != null && !cedulaCliente.isEmpty()) {
                if (!CedulaValidator.esValida(cedulaCliente)) {
                    throw new IllegalArgumentException(CedulaValidator.obtenerMensajeError(cedulaCliente));
                }
            }
            
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
            
            // Crear la venta CON cédula
            Venta venta = new Venta(usuario, cedulaCliente != null ? cedulaCliente : "", productos, total, metodoPago);
            
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
    
    /**
     * Valida una cédula
     * @param cedula Cédula a validar
     * @return String con mensaje de error, o null si es válida
     */
    public String validarCedula(String cedula) {
        return CedulaValidator.obtenerMensajeError(cedula);
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