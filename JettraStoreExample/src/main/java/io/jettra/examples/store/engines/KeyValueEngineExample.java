package io.jettra.examples.store.engines;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.core.StorageMode;
import io.jettra.store.engine.models.KeyValueEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Ejemplo completo del Motor Clave-Valor (KeyValueEngine).
 * Diseñado para cachés ultrarrápidas, sesiones distribuidas y almacenamiento
 * binario en arrays directos sin sobrecarga de wrappers de objetos.
 */
public class KeyValueEngineExample {

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("2. MOTOR CLAVE-VALOR (KeyValueEngine)");

        // 1. Base de datos en memoria RAM
        String ramDb = "cache_ram_db";
        driver.getDatabase(ramDb, StorageMode.JVM_RAM);
        ConsoleColor.printSubHeader("Ejecución en modo RAM (StorageMode.JVM_RAM)");
        testKeyValue(driver, ramDb, "sesiones_usuarios");

        // 2. Base de datos en DISK_MEMORY
        String diskDb = "cache_disk_db";
        driver.getDatabase(diskDb, StorageMode.DISK_MEMORY);
        ConsoleColor.printSubHeader("Ejecución en modo DISK_MEMORY (StorageMode.DISK_MEMORY)");
        testKeyValue(driver, diskDb, "tokens_seguridad");
    }

    private static void testKeyValue(JettraDriver driver, String dbName, String namespace) {
        KeyValueEngine kv = driver.getKeyValueEngine(dbName, namespace);

        ConsoleColor.printInfo("Base de datos", dbName + " [Modo: " + driver.getStorageMode(dbName) + "]");
        ConsoleColor.printInfo("Namespace", namespace);

        // Almacenamiento directo de payloads binarios
        String key1 = "session:user:101";
        String value1 = "{ \"userId\": 101, \"roles\": [\"ADMIN\", \"DEVELOPER\"], \"login\": \"2026-09-30T16:00:00Z\" }";
        kv.put(key1, value1.getBytes(StandardCharsets.UTF_8));

        String key2 = "session:user:102";
        String value2 = "{ \"userId\": 102, \"roles\": [\"OPERATOR\"], \"login\": \"2026-09-30T16:15:00Z\" }";
        kv.put(key2, value2.getBytes(StandardCharsets.UTF_8));

        ConsoleColor.printSuccess("2 pares Clave-Valor almacenados.");

        // Verificación de existencia y recuperación
        boolean exists = kv.containsKey(key1);
        ConsoleColor.printInfo("¿Existe '" + key1 + "'?", exists);

        byte[] retrievedBytes = kv.get(key1);
        if (retrievedBytes != null) {
            String retrievedStr = new String(retrievedBytes, StandardCharsets.UTF_8);
            ConsoleColor.printSuccess("Valor recuperado para " + key1 + ":");
            System.out.println("    " + retrievedStr);
        }

        // Inserción en lote (Batch)
        Map<String, byte[]> batch = Map.of(
                "config:max_connections", "5000".getBytes(StandardCharsets.UTF_8),
                "config:timeout_ms", "250".getBytes(StandardCharsets.UTF_8),
                "config:cluster_mode", "DYNAMIC_RING".getBytes(StandardCharsets.UTF_8)
        );
        kv.putBatch(batch);
        ConsoleColor.printSuccess("Inserción por lote (batch) completada: 3 claves añadidas.");
        ConsoleColor.printInfo("Tamaño total de claves en '" + namespace + "'", kv.size());

        // Eliminación de clave
        boolean removed = kv.remove(key2);
        ConsoleColor.printInfo("Eliminación de '" + key2 + "'", removed ? "Exitoso" : "No encontrado");
        ConsoleColor.printInfo("Tamaño final tras eliminación", kv.size());
    }
}
