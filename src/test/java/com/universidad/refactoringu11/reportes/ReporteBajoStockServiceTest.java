package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReporteBajoStockServiceTest {

    @Test
    void notificar_registraLaAlertaEnElReporte() {
        InventarioService inventario = new InventarioService();
        ReporteBajoStockService reporte = new ReporteBajoStockService(inventario);
        inventario.setNotificador(reporte);
        inventario.agregarProducto(new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 20, 5));

        inventario.actualizarInventario(1L, 17, "SALIDA");

        String texto = reporte.generarReporte();
        assertTrue(texto.contains("Producto bajo minimo: Clean Code"));
        assertTrue(texto.contains("Reordenar 12 unidades"));
    }

    @Test
    void sinMovimientos_reportaQueNoHayAlertas() {
        ReporteBajoStockService reporte = new ReporteBajoStockService(new InventarioService());
        assertTrue(reporte.generarReporte().endsWith("Sin alertas ni productos bajo stock minimo."));
    }
}