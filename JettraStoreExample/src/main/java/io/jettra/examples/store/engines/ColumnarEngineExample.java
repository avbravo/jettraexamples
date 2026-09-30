package io.jettra.examples.store.engines;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.core.StorageMode;
import io.jettra.store.engine.models.ColumnarEngine;

import java.util.List;
import java.util.Map;

/**
 * Ejemplo completo del Motor Columnar (ColumnarEngine).
 * Diseñado para analítica en tiempo real (OLAP), compresión por columnas,
 * proyecciones eficientes y operaciones vectorizadas de agregación numérica.
 */
public class ColumnarEngineExample {

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("7. MOTOR COLUMNAR (ColumnarEngine - Analítica OLAP)");

        // 1. Tabla columnar en memoria RAM
        String ramDb = "financial_analytics_ram_db";
        driver.getDatabase(ramDb, StorageMode.JVM_RAM);
        ConsoleColor.printSubHeader("Ejecución en modo RAM (StorageMode.JVM_RAM)");
        testColumnar(driver, ramDb, "ventas_trimestrales");

        // 2. Tabla columnar en DISK_MEMORY
        String diskDb = "financial_analytics_disk_db";
        driver.getDatabase(diskDb, StorageMode.DISK_MEMORY);
        ConsoleColor.printSubHeader("Ejecución en modo DISK_MEMORY (StorageMode.DISK_MEMORY)");
        testColumnar(driver, diskDb, "costos_operativos");
    }

    private static void testColumnar(JettraDriver driver, String dbName, String tableName) {
        ColumnarEngine colEngine = driver.getColumnarEngine(dbName, tableName);

        ConsoleColor.printInfo("Base de datos", dbName + " [Modo: " + driver.getStorageMode(dbName) + "]");
        ConsoleColor.printInfo("Tabla Columnar", tableName);

        // Inserción de filas estructuradas
        colEngine.appendRow(Map.of(
                "quarter", "Q1",
                "region", "Norte",
                "revenue", 125000.50,
                "units_sold", 450,
                "tax", 8750.00
        ));

        colEngine.appendRow(Map.of(
                "quarter", "Q2",
                "region", "Sur",
                "revenue", 185200.00,
                "units_sold", 620,
                "tax", 12964.00
        ));

        colEngine.appendRow(Map.of(
                "quarter", "Q3",
                "region", "Norte",
                "revenue", 210400.75,
                "units_sold", 780,
                "tax", 14728.05
        ));

        colEngine.appendRow(Map.of(
                "quarter", "Q4",
                "region", "Centro",
                "revenue", 340100.25,
                "units_sold", 1150,
                "tax", 23807.00
        ));

        ConsoleColor.printSuccess("4 registros agregados a la estructura columnar.");
        ConsoleColor.printInfo("Total filas en tabla columnar", colEngine.getRowCount());

        // Acceso directo a columna numérica continua (sin instanciar objetos fila)
        List<Double> revenues = colEngine.getNumericColumn("revenue");
        ConsoleColor.printInfo("Valores continuos en columna 'revenue'", revenues);

        // Agregación analítica vectorizada (Suma total)
        double totalRevenue = colEngine.sumColumn("revenue");
        double totalUnits = colEngine.sumColumn("units_sold");
        double totalTax = colEngine.sumColumn("tax");

        ConsoleColor.printSuccess("Resultados de Agregación Columnar:");
        ConsoleColor.printInfo("  Suma Total Revenue", "$" + String.format("%,.2f", totalRevenue));
        ConsoleColor.printInfo("  Suma Total Unidades", String.format("%,.0f", totalUnits));
        ConsoleColor.printInfo("  Suma Total Impuestos", "$" + String.format("%,.2f", totalTax));
    }
}
