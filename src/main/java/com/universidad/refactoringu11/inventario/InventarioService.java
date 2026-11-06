package com.universidad.refactoringu11.inventario;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventarioService {

    private static final String MOVIMIENTO_ENTRADA = "ENTRADA";
    private static final String MOVIMIENTO_SALIDA = "SALIDA";
    private static final int FACTOR_REORDEN = 3;

    private final List<Producto> productos = new ArrayList<>();
    private final List<String> movimientos = new ArrayList<>();

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public String actualizarInventario(Long productoId, int cantidad, String tipoMovimiento) {
        if (!esMovimientoValido(productoId, cantidad, tipoMovimiento)) return "ERROR_DATOS_INVALIDOS";
        Producto producto = buscarProductoPorId(productoId);
        if (producto == null) return "ERROR_PRODUCTO_NO_EXISTE";
        if (!tieneStockSuficiente(producto, cantidad, tipoMovimiento)) return "ERROR_STOCK_INSUFICIENTE";

        int nuevoStock = aplicarMovimientoStock(producto, cantidad, tipoMovimiento);
        registrarMovimiento(producto, cantidad, tipoMovimiento, nuevoStock);
        return generarRespuestaActualizacion(producto, nuevoStock);
    }

    private boolean esMovimientoValido(Long productoId, int cantidad, String tipoMovimiento) {
        return productoId != null
                && cantidad > 0
                && (MOVIMIENTO_ENTRADA.equals(tipoMovimiento) || MOVIMIENTO_SALIDA.equals(tipoMovimiento));
    }

    private Producto buscarProductoPorId(Long productoId) {
        for (Producto producto : productos) {
            if (producto.getId().equals(productoId)) {
                return producto;
            }
        }
        return null;
    }

    private boolean tieneStockSuficiente(Producto producto, int cantidad, String tipoMovimiento) {
        return !MOVIMIENTO_SALIDA.equals(tipoMovimiento) || producto.getStock() >= cantidad;
    }

    private int aplicarMovimientoStock(Producto producto, int cantidad, String tipoMovimiento) {
        int nuevoStock = MOVIMIENTO_ENTRADA.equals(tipoMovimiento)
                ? producto.getStock() + cantidad
                : producto.getStock() - cantidad;
        producto.setStock(nuevoStock);
        return nuevoStock;
    }

    private int calcularCantidadReorden(Producto producto, int stockActual) {
        if (stockActual >= producto.getStockMinimo()) {
            return 0;
        }
        return (producto.getStockMinimo() * FACTOR_REORDEN) - stockActual;
    }

    private void registrarMovimiento(Producto producto, int cantidad, String tipoMovimiento, int nuevoStock) {
        String registro = tipoMovimiento + " de " + cantidad + " unidades de "
                + producto.getNombre() + " -> stock final: " + nuevoStock;
        movimientos.add(registro);
        System.out.println(registro);
    }

    private String generarRespuestaActualizacion(Producto producto, int nuevoStock) {
        int cantidadReorden = calcularCantidadReorden(producto, nuevoStock);
        if (cantidadReorden > 0) {
            System.out.println("ALERTA: " + producto.getNombre()
                    + " bajo stock minimo. Reordenar " + cantidadReorden + " unidades.");
            return "OK_BAJO_STOCK_REORDEN_" + cantidadReorden;
        }
        return "OK_" + nuevoStock;
    }
}