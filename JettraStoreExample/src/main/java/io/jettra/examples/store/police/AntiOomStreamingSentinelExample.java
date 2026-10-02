package io.jettra.examples.store.police;

import io.jettra.driver.listener.JettraPoliceEventListener;
import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.core.StorageMode;
import io.jettra.store.core.StreamResponse;
import io.jettra.store.engine.models.DocumentEngine;
import io.jettra.store.engine.query.JettraSQLProcessor;
import io.jettra.store.police.JettraPoliceNotification;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Ejemplo Oficial de Prevención Anti-OOM:
 * Demuestra el uso de JettraPolice Sentinel, Streaming por Chunks y
 * Notificaciones Desacopladas con JettraPoliceEventListener en Java 25.
 */
public class AntiOomStreamingSentinelExample {

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("13. PROTECCIÓN ANTI-OOM: SENTINEL, STREAMING POR CHUNKS Y LISTENERS");

        String dbName = "streaming_sentinel_db";
        driver.getDatabase(dbName, StorageMode.JVM_RAM);

        // 1. Registro del Listener Desacoplado (JettraPoliceEventListener)
        ConsoleColor.printSubHeader("1. Registro de JettraPoliceEventListener (Desacoplado)");
        AtomicBoolean sentinelNotified = new AtomicBoolean(false);
        AtomicReference<JettraPoliceNotification> lastNotifRef = new AtomicReference<>();

        JettraPoliceEventListener listener = notification -> {
            sentinelNotified.set(true);
            lastNotifRef.set(notification);
            System.out.println(ConsoleColor.YELLOW + ConsoleColor.BOLD);
            System.out.println("    ╔══════════════════════════════════════════════════════════════════════╗");
            System.out.println("    ║ 🛡️  [EVENTO SENTINEL CAPTURADO POR JETTRAPOLICE EVENT LISTENER]     ║");
            System.out.println("    ╠══════════════════════════════════════════════════════════════════════╣");
            System.out.printf("    ║  Operación: %-15s | Colección: %-29s║%n", notification.operation(), notification.targetCollection());
            System.out.printf("    ║  Total Estimado: %-10d | Lote Seguro: %-30d║%n", notification.estimatedTotalRecords(), notification.safeBatchSize());
            System.out.printf("    ║  Saturación Heap: %5.1f%%        | RAM Disponible: %-26s║%n", notification.heapUsagePercent(), notification.availableMemoryMb() + " MB");
            System.out.println("    ║  Estrategia: Streaming por Chunks forzado para prevenir OOM.        ║");
            System.out.println("    ╚══════════════════════════════════════════════════════════════════════╝" + ConsoleColor.RESET);
        };

        driver.addPoliceEventListener(listener);
        ConsoleColor.printSuccess("Listener de JettraPolice registrado en JettraClient.");

        // 2. Poblado de colección de prueba masiva (500 documentos)
        ConsoleColor.printSubHeader("2. Poblado masivo de prueba en DocumentEngine");
        DocumentEngine catalog = driver.getDocumentEngine(dbName, "catalogo_masivo");
        int totalInsertados = 500;
        for (int i = 1; i <= totalInsertados; i++) {
            catalog.insert("item_" + i, Map.of(
                "sku", "SKU-2026-" + i,
                "nombre", "Sensor Industrial Modelo #" + i,
                "categoria", (i % 2 == 0) ? "ELECTRÓNICA" : "MECÁNICA",
                "precio", 49.99 + (i * 0.5),
                "stock", i * 10
            ));
        }
        ConsoleColor.printSuccess(totalInsertados + " documentos insertados en catalogo_masivo.");

        // 3. Streaming por Chunks Seguro (StreamResponse<T>)
        ConsoleColor.printSubHeader("3. Streaming Seguro por Chunks con Liberación de Memoria para GC");
        try (StreamResponse<Map<String, Object>> stream = driver.streamFindAll(dbName, "catalogo_masivo")) {
            ConsoleColor.printInfo("Batch Seguro Calculado", stream.getSafeBatchSize() + " registros/lote");
            ConsoleColor.printInfo("Total Estimado", stream.getTotalEstimated());
            ConsoleColor.printInfo("Sentinel Activado Proactivamente", stream.isSentinelActivated());

            AtomicInteger chunkNum = new AtomicInteger(1);
            AtomicInteger totalRecibido = new AtomicInteger(0);

            // forEachChunk libera y dereferencia explícitamente cada lote para el recolector de basura
            stream.forEachChunk(chunk -> {
                totalRecibido.addAndGet(chunk.size());
                System.out.printf("    • Chunk #%02d recibido con %d registros (Acumulado: %d)%n",
                    chunkNum.getAndIncrement(), chunk.size(), totalRecibido.get());
            });

            ConsoleColor.printSuccess("Streaming completado: " + totalRecibido.get() + " registros procesados en bloques seguros.");
        }

        // 4. Consumo Transparente Unificado (findAll)
        ConsoleColor.printSubHeader("4. Consumo Transparente mediante findAll() sin alterar firmas");
        List<Map<String, Object>> all = driver.findAll(dbName, "catalogo_masivo");
        ConsoleColor.printSuccess("driver.findAll() recuperó " + all.size() + " registros ensamblados de forma eficiente.");
        if (!all.isEmpty()) {
            ConsoleColor.printInfo("Primer Registro", all.getFirst().get("nombre"));
            ConsoleColor.printInfo("Último Registro", all.getLast().get("nombre"));
        }

        // 5. Activación de Sentinel en Consultas SQL Masivas
        ConsoleColor.printSubHeader("5. Intervención Automática en Consulta SQL no acotada");
        String sql = "SELECT * FROM catalogo_masivo";
        ConsoleColor.printInfo("Ejecutando SQL Masivo", sql);

        JettraSQLProcessor.QueryResult sqlRes = driver.sql(dbName, sql);
        ConsoleColor.printSuccess("Consulta ejecutada. Diagnóstico: " + sqlRes.message());
        ConsoleColor.printInfo("Filas Recuperadas de Forma Segura", sqlRes.rows().size());
        ConsoleColor.printInfo("Intervención Sentinel Registrada", sentinelNotified.get());

        // Limpieza del listener
        driver.removePoliceEventListener(listener);
        ConsoleColor.printSuccess("Listener de JettraPolice desregistrado con éxito.");
    }
}
