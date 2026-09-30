package io.jettra.examples.store.engines;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.core.StorageMode;
import io.jettra.store.engine.models.TimeSeriesEngine;

import java.time.Instant;
import java.util.NavigableMap;

/**
 * Ejemplo completo del Motor de Series Temporales (TimeSeriesEngine).
 * Optimizado para ingestión de métricas IoT, telemetría de servidores,
 * consultas de ventanas temporales (range queries) y cálculo de promedios instantáneos.
 */
public class TimeSeriesEngineExample {

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("5. MOTOR DE SERIES TEMPORALES (TimeSeriesEngine)");

        // 1. Métrica en memoria RAM
        String ramDb = "telemetry_ram_db";
        driver.getDatabase(ramDb, StorageMode.JVM_RAM);
        ConsoleColor.printSubHeader("Ejecución en modo RAM (StorageMode.JVM_RAM)");
        testTimeSeries(driver, ramDb, "cpu_usage_percentage");

        // 2. Métrica en DISK_MEMORY
        String diskDb = "telemetry_disk_db";
        driver.getDatabase(diskDb, StorageMode.DISK_MEMORY);
        ConsoleColor.printSubHeader("Ejecución en modo DISK_MEMORY (StorageMode.DISK_MEMORY)");
        testTimeSeries(driver, diskDb, "nvme_io_latency_ms");
    }

    private static void testTimeSeries(JettraDriver driver, String dbName, String metricName) {
        TimeSeriesEngine ts = driver.getTimeSeriesEngine(dbName, metricName);

        ConsoleColor.printInfo("Base de datos", dbName + " [Modo: " + driver.getStorageMode(dbName) + "]");
        ConsoleColor.printInfo("Métrica temporal", metricName);

        long now = System.currentTimeMillis();
        long step = 1000L; // 1 segundo por muestra

        // Simular 10 lecturas consecutivas en el tiempo
        for (int i = 0; i < 10; i++) {
            long t = now + (i * step);
            double val = 40.0 + (Math.sin(i) * 15.0); // Oscilación de telemetría
            ts.record(t, Math.round(val * 100.0) / 100.0);
        }

        ConsoleColor.printSuccess("10 puntos temporales registrados en la serie.");
        ConsoleColor.printInfo("Total de muestras en serie", ts.size());

        // Consulta de ventana de tiempo (últimos 5 segundos)
        long windowStart = now + (3 * step);
        long windowEnd = now + (8 * step);

        ConsoleColor.printInfo("Consulta de rango temporal",
                Instant.ofEpochMilli(windowStart) + " a " + Instant.ofEpochMilli(windowEnd));

        NavigableMap<Long, Double> subMap = ts.range(windowStart, windowEnd);
        for (var entry : subMap.entrySet()) {
            System.out.printf("    [%s] -> Valor: %.2f%n",
                    Instant.ofEpochMilli(entry.getKey()), entry.getValue());
        }

        // Agregación analítica de promedio (Average) en ventana
        double avg = ts.average(windowStart, windowEnd);
        ConsoleColor.printSuccess("Promedio en la ventana consultada: " + String.format("%.2f", avg));
    }
}
