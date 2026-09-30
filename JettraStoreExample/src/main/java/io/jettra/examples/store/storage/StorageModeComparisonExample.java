package io.jettra.examples.store.storage;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.core.StorageMode;
import io.jettra.store.engine.models.DocumentEngine;

import java.util.Map;

/**
 * Comparativa directa entre los modos de almacenamiento:
 * - JVM_RAM: Memoria RAM estándar de la JVM (Heap & Stack). Máxima velocidad in-memory.
 * - DISK_MEMORY: Motor directo en disco sin pausas de GC usando JettraMemory (LSM Off-Heap FFM).
 */
public class StorageModeComparisonExample {

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("10. COMPARATIVA DE MODOS: RAM (JVM_RAM) vs DISK_MEMORY");

        // 1. Base de datos en modo RAM
        String dbRam = "benchmark_ram_db";
        driver.getDatabase(dbRam, StorageMode.JVM_RAM);
        StorageMode modeRam = driver.getStorageMode(dbRam);

        ConsoleColor.printSubHeader("1. Base de datos configurada en RAM");
        ConsoleColor.printInfo("Nombre de BD", dbRam);
        ConsoleColor.printInfo("Modo Activo", modeRam.getCode() + " - " + modeRam.getDescription());

        long startRam = System.nanoTime();
        DocumentEngine ramDocs = driver.getDocumentEngine(dbRam, "transacciones");
        for (int i = 1; i <= 1000; i++) {
            ramDocs.insert("tx_ram_" + i, Map.of(
                    "tx_id", i,
                    "monto", 50.0 + (i * 0.25),
                    "tipo", (i % 2 == 0) ? "DEPOSITO" : "RETIRO",
                    "estado", "PROCESADO"
            ));
        }
        long durationRamNs = System.nanoTime() - startRam;
        double durationRamMs = durationRamNs / 1_000_000.0;
        ConsoleColor.printSuccess("1,000 registros insertados en RAM en " + String.format("%.2f", durationRamMs) + " ms");
        ConsoleColor.printInfo("Total de documentos en RAM", ramDocs.count());

        // 2. Base de datos en modo DISK_MEMORY
        String dbDisk = "benchmark_disk_db";
        driver.getDatabase(dbDisk, StorageMode.DISK_MEMORY);
        StorageMode modeDisk = driver.getStorageMode(dbDisk);

        ConsoleColor.printSubHeader("2. Base de datos configurada en DISK_MEMORY");
        ConsoleColor.printInfo("Nombre de BD", dbDisk);
        ConsoleColor.printInfo("Modo Activo", modeDisk.getCode() + " - " + modeDisk.getDescription());

        long startDisk = System.nanoTime();
        DocumentEngine diskDocs = driver.getDocumentEngine(dbDisk, "transacciones");
        for (int i = 1; i <= 1000; i++) {
            diskDocs.insert("tx_disk_" + i, Map.of(
                    "tx_id", i,
                    "monto", 50.0 + (i * 0.25),
                    "tipo", (i % 2 == 0) ? "DEPOSITO" : "RETIRO",
                    "estado", "PERSISTIDO"
            ));
        }
        long durationDiskNs = System.nanoTime() - startDisk;
        double durationDiskMs = durationDiskNs / 1_000_000.0;
        ConsoleColor.printSuccess("1,000 registros insertados en DISK_MEMORY en " + String.format("%.2f", durationDiskMs) + " ms");
        ConsoleColor.printInfo("Total de documentos en DISK_MEMORY", diskDocs.count());

        // 3. Conmutación dinámica de modo en tiempo de ejecución
        ConsoleColor.printSubHeader("3. Conmutación Dinámica de Modo");
        ConsoleColor.printInfo("Cambiando 'benchmark_ram_db' a DISK_MEMORY...", "");
        driver.setStorageMode(dbRam, StorageMode.DISK_MEMORY);
        ConsoleColor.printSuccess("Modo actualizado dinámicamente a: " + driver.getStorageMode(dbRam).getCode());

        ConsoleColor.printInfo("Cambiando 'benchmark_ram_db' de regreso a JVM_RAM...", "");
        driver.setStorageMode(dbRam, StorageMode.JVM_RAM);
        ConsoleColor.printSuccess("Modo restaurado a: " + driver.getStorageMode(dbRam).getCode());
    }
}
