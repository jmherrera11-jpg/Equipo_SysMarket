package controller;

import model.Producto;
import repository.ProductoRepository;
import repository.CategoriaRepository;

import java.util.List;

public class ProductoController {
    private ProductoRepository productoRepository;
    private CategoriaRepository categoriaRepository;
    
    public ProductoController(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }
    
    // Validaciones de negocio - MEJORADO
    public void agregarProducto(Producto producto) {
        // Validaciones adicionales
        if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("El código del producto es obligatorio");
        }
        
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        
        if (productoRepository.existeProducto(producto.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un producto con el código: " + producto.getCodigo());
        }
        
        if (producto.getPrecioCompra() <= 0) {
            throw new IllegalArgumentException("El precio de compra debe ser mayor a 0");
        }
        
        if (producto.getPrecioVenta() <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor a 0");
        }
        
        if (producto.getPrecioVenta() <= producto.getPrecioCompra()) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor al precio de compra");
        }
        
        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        
        if (producto.getStockMinimo() < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        }
        
        productoRepository.agregarProducto(producto);
    }
    
    public void actualizarProducto(String codigo, Producto producto) {
        if (!productoRepository.existeProducto(codigo)) {
            throw new IllegalArgumentException("No existe un producto con el código: " + codigo);
        }
        
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        
        if (producto.getPrecioCompra() <= 0) {
            throw new IllegalArgumentException("El precio de compra debe ser mayor a 0");
        }
        
        if (producto.getPrecioVenta() <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor a 0");
        }
        
        if (producto.getPrecioVenta() <= producto.getPrecioCompra()) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor al precio de compra");
        }
        
        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        
        if (producto.getStockMinimo() < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        }
        
        productoRepository.actualizarProducto(codigo, producto);
    }
    
    public void eliminarProducto(String codigo) {
        if (!productoRepository.existeProducto(codigo)) {
            throw new IllegalArgumentException("No existe un producto con el código: " + codigo);
        }
        
        productoRepository.eliminarProducto(codigo);
    }
    
    public List<Producto> obtenerTodosProductos() {
        return productoRepository.obtenerTodosProductos();
    }
    
    public List<Producto> buscarProductosPorCategoria(String categoria) {
        return productoRepository.buscarProductosPorCategoria(categoria);
    }
    
    public Producto buscarProductoPorCodigo(String codigo) {
        return productoRepository.buscarProductoPorCodigo(codigo);
    }
    
    public List<String> obtenerCategorias() {
        return categoriaRepository.obtenerTodasCategorias();
    }
    
    public boolean existeProducto(String codigo) {
        return productoRepository.existeProducto(codigo);
    }
}
