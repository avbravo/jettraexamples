package io.jettra.examples.store.engines;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.core.StorageMode;
import io.jettra.store.engine.models.DocumentEngine;

import java.util.*;

/**
 * Ejemplo completo del Motor de Documentos NoSQL (DocumentEngine).
 * Muestra almacenamiento jerárquico tipo JSON/Map, consultas por ID,
 * streaming perezoso sin sobrecarga de Heap y funcionamiento en RAM y DISK_MEMORY.
 */
public class DocumentEngineExample {

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("1. MOTOR DE DOCUMENTOS (DocumentEngine)");

        // 1. Demostración en modo JVM_RAM (Memoria RAM)
        ConsoleColor.printSubHeader("Ejecución en modo RAM (StorageMode.JVM_RAM)");
        String ramDb = "ecommerce_ram_db";
        driver.getDatabase(ramDb, StorageMode.JVM_RAM);
        testDocumentEngine(driver, ramDb, "productos");

        // 2. Demostración en modo DISK_MEMORY (Directo a disco Off-Heap LSM)
        ConsoleColor.printSubHeader("Ejecución en modo DISK_MEMORY (StorageMode.DISK_MEMORY)");
        String diskDb = "ecommerce_disk_db";
        driver.getDatabase(diskDb, StorageMode.DISK_MEMORY);
        testDocumentEngine(driver, diskDb, "facturas");
    }

    private static void testDocumentEngine(JettraDriver driver, String dbName, String collection) {
        DocumentEngine engine = driver.getDocumentEngine(dbName, collection);

        ConsoleColor.printInfo("Base de datos", dbName + " [Modo: " + driver.getStorageMode(dbName) + "]");
        ConsoleColor.printInfo("Colección activa", collection);

        // Inserción de documentos con datos anidados
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("sku", "SRV-2026-X1");
        doc1.put("nombre", "Servidor Edge Jettra 25");
        doc1.put("precio", 3499.99);
        doc1.put("stock", 45);
        doc1.put("especificaciones", Map.of("cores", 64, "ram_gb", 256, "nvme_tb", 8));
        doc1.put("tags", List.of("enterprise", "cloud", "java25"));

        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("sku", "SRV-2026-X2");
        doc2.put("nombre", "Micro-Nodo IoT Jettra");
        doc2.put("precio", 599.50);
        doc2.put("stock", 120);
        doc2.put("especificaciones", Map.of("cores", 8, "ram_gb", 16, "nvme_tb", 1));
        doc2.put("tags", List.of("iot", "edge", "virtual-threads"));

        engine.insert("doc-001", doc1);
        engine.insert("doc-002", doc2);

        ConsoleColor.printSuccess("2 documentos insertados con éxito.");
        ConsoleColor.printInfo("Total documentos en colección", engine.count());

        // Búsqueda puntual por ID
        Map<String, Object> found = engine.findById("doc-001");
        if (found != null) {
            ConsoleColor.printSuccess("Documento 'doc-001' recuperado:");
            ConsoleColor.printInfo("  Nombre", found.get("nombre"));
            ConsoleColor.printInfo("  Precio", found.get("precio"));
            ConsoleColor.printInfo("  Especificaciones", found.get("especificaciones"));
        }

        // Iteración mediante Lazy List (sin duplicar Heap)
        ConsoleColor.printInfo("Recorrido mediante Lazy findAll()", "");
        for (Map<String, Object> doc : engine.findAll()) {
            System.out.printf("    -> ID: %s | Nombre: %s | Precio: %s%n",
                    doc.get("_id"), doc.get("nombre"), doc.get("precio"));
        }

        // Filtrado y procesamiento reactivo con Stream API de Java 25
        long countCaros = engine.stream()
                .filter(d -> ((Number) d.get("precio")).doubleValue() > 1000.0)
                .count();
        ConsoleColor.printInfo("Productos con precio > $1,000", countCaros);
    }
}
