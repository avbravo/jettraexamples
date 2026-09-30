package io.jettra.examples.store.engines;

import com.jettra.memory.engine.StorageMetrics;
import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.core.StorageMode;

import java.nio.charset.StandardCharsets;

/**
 * Ejemplo del Motor Off-Heap JettraMemory (Project Panama FFM API).
 * Permite almacenar y recuperar buffers binarios nativos fuera del montículo (Heap),
 * eliminando el 100% de la presión de recolección de basura (Zero-GC pressure).
 */
public class MemoryOffHeapExample {

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("9. MOTOR OFF-HEAP BINARIO (JettraMemory FFM API)");

        String dbName = "offheap_binary_db";
        driver.getDatabase(dbName, StorageMode.DISK_MEMORY);

        ConsoleColor.printInfo("Base de datos", dbName + " [Modo: " + driver.getStorageMode(dbName) + "]");

        try {
            // 1. Inserción de payload binario directamente en memoria nativa
            String binaryKey = "documento:pdf:reporte_2026";
            byte[] simulatedPdfBytes = "PDF-1.7 %%%% Simulacion de bytes de un archivo binario nativo fuera del Heap %%%%".getBytes(StandardCharsets.UTF_8);

            ConsoleColor.printInfo("Almacenando buffer binario en Off-Heap",
                    binaryKey + " (" + simulatedPdfBytes.length + " bytes)");

            driver.putBinary(dbName, binaryKey, simulatedPdfBytes);
            ConsoleColor.printSuccess("Payload binario persistido en arena nativa sin tocar el Heap.");

            // 2. Recuperación instantánea del buffer
            byte[] retrieved = driver.getBinary(dbName, binaryKey);
            if (retrieved != null) {
                String content = new String(retrieved, StandardCharsets.UTF_8);
                ConsoleColor.printSuccess("Buffer recuperado desde memoria Off-Heap (" + retrieved.length + " bytes):");
                ConsoleColor.printInfo("  Contenido", content);
            }

            // 3. Inspección de métricas de memoria nativa
            StorageMetrics metrics = driver.getMemoryMetrics(dbName);
            if (metrics != null) {
                ConsoleColor.printSubHeader("Telemetría de Memoria Off-Heap (Panama FFM)");
                ConsoleColor.printInfo("Bytes Off-Heap asignados", metrics.totalAllocatedBytes() + " bytes");
                ConsoleColor.printInfo("Bytes Off-Heap activos", metrics.activeBytes() + " bytes");
                ConsoleColor.printInfo("Operaciones de lectura", metrics.readOperations());
                ConsoleColor.printInfo("Operaciones de escritura", metrics.writeOperations());
            }

            // 4. Compactación en caliente fuera de banda
            boolean compacted = driver.compactMemory(dbName);
            ConsoleColor.printInfo("Compactación en caliente ejecutada", compacted ? "Exitosa" : "No requerida");

        } catch (Exception ex) {
            ConsoleColor.printWarning("Aviso Off-Heap: " + ex.getMessage());
        }
    }
}
