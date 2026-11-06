package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.InventarioService;
import com.universidad.refactoringu11.inventario.Producto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteBajoStockService {

    private final InventarioService inventarioService;

    public ReporteBajoStockService(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    public String generarReporte() {
        List<Producto> productosBajoMinimo = inventarioService.obtenerProductosBajoStockMinimo();
        if (productosBajoMinimo.isEmpty()) {
            return "REPORTE BAJO STOCK: Sin productos en nivel critico.";
        }
        StringBuilder sb = new StringBuilder("REPORTE BAJO STOCK:\n");
        for (Producto producto : productosBajoMinimo) {
            sb.append("- ")
              .append(producto.getNombre())
              .append(" (stock actual: ")
              .append(producto.getStock())
              .append(", minimo: ")
              .append(producto.getStockMinimo())
              .append(")\n");
        }
        return sb.toString().trim();
    }
}