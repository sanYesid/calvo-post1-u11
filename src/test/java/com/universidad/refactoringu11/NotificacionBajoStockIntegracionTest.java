package com.universidad.refactoringu11;

import com.universidad.refactoringu11.inventario.*;
import com.universidad.refactoringu11.reportes.ReporteBajoStockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotificacionBajoStockIntegracionTest {

    @Autowired private InventarioService inventarioService;
    @Autowired private ReporteBajoStockService reporteBajoStockService;

    @Test
    void salidaBajoElMinimo_notificaAlModuloDeReportes() {
        inventarioService.agregarProducto(
                new Producto(100L, "Monitor", TipoProducto.ELECTRONICO, 300.0, 10, 4));

        assertEquals("OK_BAJO_STOCK_REORDEN_10",
                inventarioService.actualizarInventario(100L, 8, "SALIDA"));
        assertTrue(reporteBajoStockService.generarReporte().contains("Reordenar 10 unidades"));
    }
}