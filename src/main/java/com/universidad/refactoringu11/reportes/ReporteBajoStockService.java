package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.InventarioService;
import com.universidad.refactoringu11.inventario.NotificadorBajoStock;
import com.universidad.refactoringu11.inventario.Producto;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteBajoStockService implements NotificadorBajoStock {

    private final InventarioService inventarioService;
    private final List<String> alertasRegistradas = new ArrayList<>();

    public ReporteBajoStockService(@Lazy InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @Override
    public void notificar(Producto producto, int cantidadReorden) {
        String alerta = "ALERTA REPORTE: Producto '" + producto.getNombre()
                + "' (stock actual: " + producto.getStock()
                + ", minimo: " + producto.getStockMinimo()
                + ") -> Reordenar " + cantidadReorden + " unidades.";
        alertasRegistradas.add(alerta);
    }

    public String generarReporte() {
        StringBuilder reporte = new StringBuilder("=== REPORTE DE BAJO STOCK ===\n");
        List<Producto> productosBajoMinimo = inventarioService.obtenerProductosBajoStockMinimo();

        for (Producto producto : productosBajoMinimo) {
            reporte.append("- Producto bajo minimo: ")
                   .append(producto.getNombre())
                   .append(" (Stock: ")
                   .append(producto.getStock())
                   .append(" / Minimo: ")
                   .append(producto.getStockMinimo())
                   .append(")\n");
        }

        for (String alerta : alertasRegistradas) {
            reporte.append(alerta).append("\n");
        }

        if (productosBajoMinimo.isEmpty() && alertasRegistradas.isEmpty()) {
            reporte.append("Sin alertas ni productos bajo stock minimo.");
        }

        return reporte.toString().trim();
    }
}