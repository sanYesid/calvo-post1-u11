package com.universidad.refactoringu11.inventario;

public enum TipoProducto {

    LIBRO {
        @Override
        public double calcularRecargoEnvio(double precio) {
            return 0.0;
        }

        @Override
        public String formatearEtiqueta(String nombreProducto) {
            return "[LIBRO] " + nombreProducto + " - Exento de IVA";
        }
    },
    ELECTRONICO {
        @Override
        public double calcularRecargoEnvio(double precio) {
            return precio * 0.08;
        }

        @Override
        public String formatearEtiqueta(String nombreProducto) {
            return "[ELECTRO] " + nombreProducto + " - Garantia 1 ano";
        }
    },
    ROPA {
        @Override
        public double calcularRecargoEnvio(double precio) {
            return precio * 0.03;
        }

        @Override
        public String formatearEtiqueta(String nombreProducto) {
            return "[ROPA] " + nombreProducto + " - Ver talla";
        }
    },
    ALIMENTO {
        @Override
        public double calcularRecargoEnvio(double precio) {
            return 2.50;
        }

        @Override
        public String formatearEtiqueta(String nombreProducto) {
            return "[ALIMENTO] " + nombreProducto + " - Perecedero";
        }
    };

    public abstract double calcularRecargoEnvio(double precio);

    public abstract String formatearEtiqueta(String nombreProducto);
}