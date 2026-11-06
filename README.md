# Post-contenido — Refactorización Guiada por Pruebas: Code Smells, Paquetes y Acoplamiento en un Proyecto Java Real

**Unidad 11 · Patrones de Diseño de Software**  
**Proyecto:** `refactoring-u11` (`com.universidad.refactoringu11`)  
**Stack:** Java 17 · Spring Boot 3.x · Maven 3.9+ · JUnit 5 · ArchUnit 1.3.0

## Parte 1: Red de Seguridad y Refactorización Guiada por Evidencia

### 1. Objetivo de la Parte 1
Escribir pruebas de caracterización que documenten el comportamiento actual de un código con *code smells* reales antes de modificarlo, y aplicar de forma autónoma la técnica de refactorización de Martin Fowler correspondiente a cada *smell* identificado, verificando con las mismas pruebas que el comportamiento observable del sistema no cambia.

### 2. Diagnóstico y Evidencia Cuantitativa Antes vs. Después (Parte 1)

#### 2.1. Code Smell 1: *Long Method* (Bloater) en `InventarioService.actualizarInventario()`
- **Diagnóstico inicial:** El método `actualizarInventario(Long, int, String)` tenía **38 líneas en su cuerpo** (más de 45 líneas contando firma y espacios) y una **complejidad ciclomática (CC) de 7**, mezclando **5 responsabilidades distintas** en un único bloque monolítico:
  1. Validación de parámetros de entrada (`productoId`, `cantidad`, `tipoMovimiento`).
  2. Búsqueda secuencial del producto en la colección interna.
  3. Cálculo y actualización del stock (con verificación de saldo insuficiente en salidas).
  4. Cálculo de la cantidad de reorden cuando el stock cae por debajo del mínimo (`stockMinimo * 3 - nuevoStock`).
  5. Registro de auditoría en el historial de movimientos y notificación/retorno de alerta de bajo stock.
- **Técnica de Fowler elegida:** **Extract Method** (Composición de Métodos) combinada con **Replace Nested Conditional with Guard Clauses** (Cláusulas de Guarda).

| Métrica / Indicador | Antes de Refactorizar (Paso 1) | Después de Refactorizar (Paso 3) | Mejora Obtenida |
| :--- | :--- | :--- | :--- |
| **Líneas de `actualizarInventario()`** | 38 líneas de cuerpo (>35 líneas) | **8 líneas** de cuerpo (<= 10 líneas) | Reducción del **78.9%** en longitud del método |
| **Responsabilidades en `actualizarInventario()`** | 5 responsabilidades mezcladas | **1 responsabilidad** (orquestación de alto nivel) | Separación en 6 métodos privados cohesivos (SRP) |
| **Complejidad Ciclomática (CC) de `actualizarInventario()`** | CC = 7 | **CC = 4** (solo cláusulas de guarda planas) | Reducción de ramificación anidada; CC <= 3 en cada método extraído |
| **Pruebas de caracterización (`InventarioServiceTest`)** | 6 tests en verde (sin modificar) | **6 tests en verde (sin modificar)** | 100% de preservación del comportamiento observable |

**Métodos privados extraídos con responsabilidad única:**
- `esMovimientoValido(Long, int, String)`: valida precondiciones de entrada.
- `buscarProductoPorId(Long)`: localiza el producto en memoria.
- `tieneStockSuficiente(Producto, int, String)`: verifica disponibilidad antes de descontar stock.
- `aplicarMovimientoStock(Producto, int, String)`: calcula y actualiza el nuevo stock del producto.
- `calcularCantidadReorden(Producto, int)`: calcula cuántas unidades deben reordenarse si cae bajo el mínimo.
- `registrarMovimiento(Producto, int, String, int)`: registra la trazabilidad de auditoría.
- `generarRespuestaActualizacion(Producto, int)`: evalúa si requiere alerta de reorden y formatea el código de retorno.

#### 2.2. Code Smell 2: *Switch Statements* Duplicados en `ClasificadorProducto` y `EtiquetadoProducto`
- **Diagnóstico inicial:** Tanto `ClasificadorProducto.calcularRecargoEnvio(Producto)` como `EtiquetadoProducto.generarEtiqueta(Producto)` contenían exactamente la misma estructura `switch (producto.getTipo())` con las mismas **4 ramas (`LIBRO`, `ELECTRONICO`, `ROPA`, `ALIMENTO`)** y el mismo bloque `default`. Esta **duplicación estructural del 100%** sobre el tipo de producto provocaba además *Shotgun Surgery*: añadir un nuevo tipo obligaba a buscar y modificar manualmente ambas clases.
- **Técnica de Fowler elegida:** **Replace Conditional with Polymorphism** (Reemplazar Condicional con Polimorfismo) mediante métodos abstractos implementados por cada constante en el `enum TipoProducto`.

| Métrica / Indicador | Antes de Refactorizar (Paso 1) | Después de Refactorizar (Paso 4) | Mejora Obtenida |
| :--- | :--- | :--- | :--- |
| **Sentencias `switch` / `if-else` sobre `TipoProducto`** | 2 bloques `switch` (4 `case` + `default` cada uno) | **0 bloques `switch` / `if-else`** en todo el proyecto | Eliminación del **100%** de condicionales de tipo |
| **Porcentaje de duplicación estructural del condicional** | **100%** (misma estructura repetida en 2 clases) | **0%** (delegación polimórfica de 1 línea) | Cumplimiento estricto del principio Open/Closed (OCP) |
| **Complejidad Ciclomática en `calcularRecargoEnvio` y `generarEtiqueta`** | CC = 5 en cada método (10 en total) | **CC = 1** en cada método (2 en total) | Reducción del **80%** en complejidad ciclomática |
| **Líneas de cuerpo en `calcularRecargoEnvio` y `generarEtiqueta`** | 9 líneas por método | **1 línea** por método (`return producto.getTipo()...`) | Delegación directa con firma pública intacta |
| **Clases a modificar al agregar un nuevo `TipoProducto`** | 3 clases (`TipoProducto`, `ClasificadorProducto`, `EtiquetadoProducto`) | **1 sola clase** (`TipoProducto`) | Eliminación de *Shotgun Surgery* |

### 3. Resultado de `mvn test` Antes y Después de Cada Refactorización (Parte 1)

| Momento de Ejecución | Comando Ejecutado | Tests Ejecutados | Fallos / Errores | Estado de las Pruebas |
| :--- | :--- | :--- | :--- | :--- |
| **Paso 2:** Red de seguridad sobre código original (sin refactorizar) | `mvn test` | 14 (`InventarioServiceTest`: 6, `ClasificadorProductoTest`: 4, `EtiquetadoProductoTest`: 4) | `Failures: 0, Errors: 0` | `BUILD SUCCESS` (Verde) |
| **Paso 3:** Tras eliminar el *Long Method* en `InventarioService` | `mvn test` | 14 (0 líneas de test modificadas) | `Failures: 0, Errors: 0` | `BUILD SUCCESS` (Verde) |
| **Paso 4:** Tras eliminar los *Switch Statements* con Polimorfismo | `mvn test` | 14 (0 líneas de test modificadas) | `Failures: 0, Errors: 0` | `BUILD SUCCESS` (Verde) |

```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.universidad.refactoringu11.inventario.ClasificadorProductoTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.universidad.refactoringu11.inventario.EtiquetadoProductoTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.universidad.refactoringu11.inventario.InventarioServiceTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```


## Parte 2: Diagnóstico y Resolución de Acoplamiento entre Paquetes

### 1. Diagnóstico del Ciclo de Dependencias con ArchUnit (Paso 2 — Prueba en Rojo)

Al integrar el módulo `reportes` (`ReporteBajoStockService`) e invocarlo directamente desde `InventarioService`, las 14 pruebas de negocio de la Parte 1 continúan pasando en verde, pero se introduce una **dependencia cíclica bidireccional entre paquetes (`inventario <-> reportes`)**:

```mermaid
graph LR
    subgraph "ANTES: Dependencia Cíclica (Acoplamiento Bidireccional)"
        I["Paquete: inventario<br/>(InventarioService, Producto)"]
        R["Paquete: reportes<br/>(ReporteBajoStockService)"]
        I -- "importa e invoca a<br/>ReporteBajoStockService" --> R
        R -- "importa e inyecta a<br/>InventarioService y Producto" --> I
    end

```