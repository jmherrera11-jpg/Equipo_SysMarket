package controller;

import model.Producto; 
import model.Venta;
import repository.ProductoRepository;
import repository.VentaRepository;
import repository.CategoriaRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

public class GerenteController {
    private ProductoRepository productoRepository;
    private VentaRepository ventaRepository;
    private CategoriaRepository categoriaRepository;
    
    public GerenteController(ProductoRepository productoRepository, 
                           VentaRepository ventaRepository,
                           CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
        this.categoriaRepository = categoriaRepository;
    }
    
    public List<Producto> obtenerProductosStockBajo() {
        return productoRepository.obtenerTodosProductos().stream()
                .filter(p -> p.getStock() <= p.getStockMinimo())
                .collect(Collectors.toList());
    }
    
    public List<Venta> obtenerVentasPorFecha(String fechaInicio, String fechaFin) {
        return ventaRepository.obtenerVentasPorRangoFechas(fechaInicio, fechaFin);
    }
    
    public String generarReporteVentas(String fechaInicio, String fechaFin) {
        List<Venta> ventas = obtenerVentasPorFecha(fechaInicio, fechaFin);
        double totalVentas = ventas.stream().mapToDouble(Venta::getTotal).sum();
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE VENTAS\n");
        reporte.append("Período: ").append(fechaInicio).append(" a ").append(fechaFin).append("\n");
        reporte.append("========================================\n");
        reporte.append(String.format("Total de ventas: %d\n", ventas.size()));
        reporte.append(String.format("Ingreso total: S/. %.2f\n", totalVentas));
        reporte.append("\nDetalle por categoría:\n");
        
        // Agrupar ventas por categoría 
        Map<String, Double> ventasPorCategoria = ventas.stream()
                .collect(Collectors.groupingBy(
                    v -> "General", 
                    Collectors.summingDouble(Venta::getTotal)
                ));
        
        ventasPorCategoria.forEach((categoria, total) -> {
            reporte.append(String.format("- %s: S/. %.2f\n", categoria, total));
        });
        
        return reporte.toString();
    }
    
    public String generarReporteStock() {
        List<Producto> productosStockBajo = obtenerProductosStockBajo();
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE STOCK CRÍTICO\n");
        reporte.append("========================================\n");
        
        if (productosStockBajo.isEmpty()) {
            reporte.append("No hay productos con stock crítico.\n");
        } else {
            reporte.append(String.format("Productos con stock crítico: %d\n\n", productosStockBajo.size()));
            for (Producto producto : productosStockBajo) {
                reporte.append(String.format("- %s (Código: %s)\n", producto.getNombre(), producto.getCodigo()));
                reporte.append(String.format("  Stock actual: %d | Stock mínimo: %d\n", 
                    producto.getStock(), producto.getStockMinimo()));
            }
        }
        
        return reporte.toString();
    }
    
    public String generarReporteProductosMasVendidos(String fechaInicio, String fechaFin) {
    	try {
            List<Venta> ventas = obtenerVentasPorFecha(fechaInicio, fechaFin);
            
            // Contar productos vendidos
            Map<String, Integer> productosVendidos = new HashMap<>();
            for (Venta venta : ventas) {
                for (Producto producto : venta.getProductos()) {
                    String nombreProducto = producto.getNombre();
                    productosVendidos.put(nombreProducto, 
                        productosVendidos.getOrDefault(nombreProducto, 0) + 1);
                }
            }
            
            // Ordenar por cantidad vendida
            List<Map.Entry<String, Integer>> sorted = productosVendidos.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
            
            StringBuilder reporte = new StringBuilder();
            reporte.append("REPORTE DE PRODUCTOS MÁS VENDIDOS\n");
            reporte.append("Período: ").append(fechaInicio).append(" a ").append(fechaFin).append("\n");
            reporte.append("========================================\n");
            
            if (sorted.isEmpty()) {
                reporte.append("No hay ventas en el período seleccionado.\n");
            } else {
                int contador = 1;
                for (Map.Entry<String, Integer> entry : sorted) {
                    reporte.append(String.format("%d. %s - %d unidades\n", contador++, entry.getKey(), entry.getValue()));
                }
            }
            
            return reporte.toString();
        } catch (Exception e) {
            return "Error al generar reporte de productos más vendidos: " + e.getMessage();
        }
    }

    public String generarReporteRentabilidad() {
        try {
            List<Producto> productos = productoRepository.obtenerTodosProductos();
            
            StringBuilder reporte = new StringBuilder();
            reporte.append("REPORTE DE RENTABILIDAD POR PRODUCTO\n");
            reporte.append("========================================\n");
            reporte.append("Producto          | Margen % | Rentabilidad\n");
            reporte.append("-----------------------------------------\n");
            
            for (Producto producto : productos) {
                double margen = ((producto.getPrecioVenta() - producto.getPrecioCompra()) / producto.getPrecioCompra()) * 100;
                String rentabilidad;
                
                if (margen >= 30) rentabilidad = "ALTA";
                else if (margen >= 15) rentabilidad = "MEDIA";
                else rentabilidad = "BAJA";
                
                reporte.append(String.format("%-18s | %7.1f%% | %s\n", 
                    producto.getNombre(), margen, rentabilidad));
            }
            
            return reporte.toString();
        } catch (Exception e) {
            return "Error al generar reporte de rentabilidad: " + e.getMessage();
        }
    }

    public Map<String, Object> obtenerEstadisticasDashboard() {
        try {
            List<Venta> ventas = ventaRepository.obtenerTodasVentas();
            List<Producto> productos = productoRepository.obtenerTodosProductos();
            
            double totalVentas = ventas.stream().mapToDouble(Venta::getTotal).sum();
            long productosStockCritico = productos.stream()
                    .filter(p -> p.getStock() <= p.getStockMinimo())
                    .count();
            
            // Calcular producto más vendido
            String productoMasVendido = "Sin datos";
            if (!ventas.isEmpty()) {
                Map<String, Integer> productosVendidos = new HashMap<>();
                for (Venta venta : ventas) {
                    for (Producto producto : venta.getProductos()) {
                        String nombre = producto.getNombre();
                        productosVendidos.put(nombre, productosVendidos.getOrDefault(nombre, 0) + 1);
                    }
                }
                if (!productosVendidos.isEmpty()) {
                    var maxEntry = productosVendidos.entrySet().stream()
                        .max(Map.Entry.comparingByValue());
                    if (maxEntry.isPresent()) {
                        productoMasVendido = maxEntry.get().getKey();
                    }
                }
            }
            
            // Calcular categoría más vendida
            String categoriaMasVendida = "Sin datos";
            if (!ventas.isEmpty()) {
                Map<String, Integer> categoriasVendidas = new HashMap<>();
                for (Venta venta : ventas) {
                    for (Producto producto : venta.getProductos()) {
                        String categoria = producto.getCategoria();
                        categoriasVendidas.put(categoria, categoriasVendidas.getOrDefault(categoria, 0) + 1);
                    }
                }
                if (!categoriasVendidas.isEmpty()) {
                    var maxEntry = categoriasVendidas.entrySet().stream()
                        .max(Map.Entry.comparingByValue());
                    if (maxEntry.isPresent()) {
                        categoriaMasVendida = maxEntry.get().getKey();
                    }
                }
            }
            
            return Map.of(
                "totalVentas", totalVentas,
                "productoMasVendido", productoMasVendido,
                "categoriaMasVendida", categoriaMasVendida,
                "stockCritico", (int) productosStockCritico
            );
        } catch (Exception e) {
            // En caso de error, retornar valores por defecto
            return Map.of(
                "totalVentas", 0.0,
                "productoMasVendido", "No disponible",
                "categoriaMasVendida", "No disponible", 
                "stockCritico", 0
            );
        }
    }
}