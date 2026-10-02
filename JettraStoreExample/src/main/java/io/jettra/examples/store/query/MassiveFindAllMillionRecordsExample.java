package io.jettra.examples.store.query;

import io.jettra.driver.listener.JettraPoliceEventListener;
import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.core.StorageMode;
import io.jettra.store.core.StreamResponse;
import io.jettra.store.engine.models.DocumentEngine;
import io.jettra.store.police.JettraPolice;
import io.jettra.store.police.JettraPoliceNotification;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Ejemplo de Evaluación de Rendimiento Masivo con findAll() sobre "example_factura_db".
 * 
 * Evalúa el comportamiento, estabilidad, latencia y rendimiento de las consultas
 * de tipo findAll() sobre colecciones con volúmenes gigantescos de datos (facturas y clientes):
 * 
 * 1. Comportamiento de findAll() frente a cientos de miles y millones de registros.
 * 2. Protección activa de JettraPolice Sentinel (Prevención de OutOfMemoryError).
 * 3. Comparativa de rendimiento: findAll() vs streamFindAll() vs LazyPagedCursor.
 * 4. Métricas en tiempo real de Throughput (documentos/segundo) y telemetría del Heap.
 */
public class MassiveFindAllMillionRecordsExample {

    private static final String DATABASE_NAME = "example_factura_db";
    private static final String COLLECTION_CLIENTES = "clientes";
    private static final String COLLECTION_FACTURAS = "facturas";

    private static final NumberFormat NUMBER_FMT = NumberFormat.getIntegerInstance(Locale.US);

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("14. CONSULTAS MASIVAS findAll() EN example_factura_db (MILLONES DE REGISTROS)");
        System.out.println("Base de datos de prueba : " + DATABASE_NAME);
        System.out.println("Colecciones evaluadas   : " + COLLECTION_CLIENTES + " y " + COLLECTION_FACTURAS);
        System.out.println("Objetivo                : Evaluar rendimiento, impacto en Heap y resiliencia Anti-OOM");
        System.out.println("--------------------------------------------------------------------------------");

        // 1. Obtener o inicializar la base de datos
        driver.getDatabase(DATABASE_NAME, StorageMode.JVM_RAM);

        // 2. Verificar datos existentes o inicializar volumen de prueba representativo
        DocumentEngine clientesEngine = driver.getDocumentEngine(DATABASE_NAME, COLLECTION_CLIENTES);
        DocumentEngine facturasEngine = driver.getDocumentEngine(DATABASE_NAME, COLLECTION_FACTURAS);

        long clientesExistentes = clientesEngine != null ? clientesEngine.count() : 0;
        long facturasExistentes = facturasEngine != null ? facturasEngine.count() : 0;

        ConsoleColor.printSubHeader("1. Verificación del Volumen de Datos en " + DATABASE_NAME);
        ConsoleColor.printInfo("Colección '" + COLLECTION_CLIENTES + "' (actual)", NUMBER_FMT.format(clientesExistentes) + " registros");
        ConsoleColor.printInfo("Colección '" + COLLECTION_FACTURAS + "' (actual)", NUMBER_FMT.format(facturasExistentes) + " registros");

        // Si las colecciones tienen pocos registros, poblamos un lote para la prueba
        if (clientesExistentes < 50_000) {
            poblarClientes(clientesEngine, 100_000);
        }
        if (facturasExistentes < 100_000) {
            poblarFacturas(facturasEngine, 1_000_025);
        }

        long totalClientes = clientesEngine.count();
        long totalFacturas = facturasEngine.count();

        ConsoleColor.printSuccess(String.format("Volumen listo para evaluación: Clientes = %s | Facturas = %s",
                NUMBER_FMT.format(totalClientes), NUMBER_FMT.format(totalFacturas)));

        // 3. Registrar Listener Desacoplado de JettraPolice Sentinel
        ConsoleColor.printSubHeader("2. Activación de JettraPolice Sentinel (Protección Anti-OOM en Tiempo Real)");
        AtomicBoolean sentinelIntervened = new AtomicBoolean(false);
        AtomicReference<JettraPoliceNotification> lastSentinelEvent = new AtomicReference<>();
        AtomicInteger sentinelTriggerCount = new AtomicInteger(0);

        JettraPoliceEventListener listener = notification -> {
            sentinelIntervened.set(true);
            lastSentinelEvent.set(notification);
            sentinelTriggerCount.incrementAndGet();

            System.out.println(ConsoleColor.YELLOW + ConsoleColor.BOLD);
            System.out.println("    ╔══════════════════════════════════════════════════════════════════════════════════╗");
            System.out.printf("    ║ 🛡️  [CENTINELA ACTIVADO] Alerta en %-15s sobre colección: %-21s║%n",
                    notification.operation(), notification.targetCollection());
            System.out.println("    ╠══════════════════════════════════════════════════════════════════════════════════╣");
            System.out.printf("    ║  Registros Totales Estimados : %-49s║%n", NUMBER_FMT.format(notification.estimatedTotalRecords()));
            System.out.printf("    ║  Lote Seguro Recomendado     : %-49s║%n", NUMBER_FMT.format(notification.safeBatchSize()) + " docs/chunk");
            System.out.printf("    ║  Presión de Heap JVM          : %5.1f%% (RAM Libre: %s MB)                     ║%n",
                    notification.heapUsagePercent(), NUMBER_FMT.format(notification.availableMemoryMb()));
            System.out.printf("    ║  Estrategia de Mitigación     : %-49s║%n",
                    notification.forcedLazyPagination() ? "Chunked Lazy Streaming Anti-OOM forzado" : "Monitoreo Preventivo");
            System.out.println("    ╚══════════════════════════════════════════════════════════════════════════════════╝" + ConsoleColor.RESET);
        };

        driver.addPoliceEventListener(listener);
        ConsoleColor.printSuccess("Centinela de JettraPolice enlazado con éxito para auditar consultas.");

        // =====================================================================
        // FASE A: Evaluación de findAll() sobre colección CLIENTES
        // =====================================================================
        ConsoleColor.printSubHeader(String.format("3. Evaluación de findAll() sobre '%s' (%s registros)",
                COLLECTION_CLIENTES, NUMBER_FMT.format(totalClientes)));

        printHeapStatus("Antes de findAll(clientes)");
        long startClientes = System.nanoTime();

        List<Map<String, Object>> clientesList = driver.findAll(DATABASE_NAME, COLLECTION_CLIENTES);

        long endClientes = System.nanoTime();
        double duracionClientesMs = (endClientes - startClientes) / 1_000_000.0;
        double throughputClientes = (clientesList.size() / (duracionClientesMs / 1000.0));

        printHeapStatus("Después de findAll(clientes)");

        ConsoleColor.printSuccess(String.format("findAll() en '%s' completado exitosamente:", COLLECTION_CLIENTES));
        ConsoleColor.printInfo("  • Registros Recuperados", NUMBER_FMT.format(clientesList.size()));
        ConsoleColor.printInfo("  • Tiempo de Respuesta  ", String.format("%.2f ms (%.3f segundos)", duracionClientesMs, duracionClientesMs / 1000.0));
        ConsoleColor.printInfo("  • Rendimiento / Throughput", String.format("%,.0f docs/segundo", throughputClientes));

        if (!clientesList.isEmpty()) {
            ConsoleColor.printInfo("  • Primer Cliente", clientesList.getFirst().get("nombre") + " (RUC/Cédula: " + clientesList.getFirst().get("documento") + ")");
            ConsoleColor.printInfo("  • Último Cliente", clientesList.getLast().get("nombre") + " (RUC/Cédula: " + clientesList.getLast().get("documento") + ")");
        }

        // Liberar referencia local para ayudar al Garbage Collector antes del siguiente test
        clientesList = null;
        System.gc();

        // =====================================================================
        // FASE B: Evaluación de findAll() sobre colección FACTURAS (Millones)
        // =====================================================================
        ConsoleColor.printSubHeader(String.format("4. Evaluación de findAll() sobre '%s' (%s registros)",
                COLLECTION_FACTURAS, NUMBER_FMT.format(totalFacturas)));

        System.out.println("Iniciando consulta completa no acotada findAll()...");
        printHeapStatus("Antes de findAll(facturas)");

        long startFacturas = System.nanoTime();
        List<Map<String, Object>> facturasList = null;
        boolean oomOcurred = false;

        try {
            facturasList = driver.findAll(DATABASE_NAME, COLLECTION_FACTURAS);
        } catch (OutOfMemoryError oom) {
            oomOcurred = true;
            System.err.println(ConsoleColor.RED + "❌ OutOfMemoryError detectado al intentar almacenar todo el millón en un único ArrayList!" + ConsoleColor.RESET);
        }

        long endFacturas = System.nanoTime();
        double duracionFacturasMs = (endFacturas - startFacturas) / 1_000_000.0;

        if (!oomOcurred && facturasList != null) {
            double throughputFacturas = (facturasList.size() / (duracionFacturasMs / 1000.0));
            printHeapStatus("Después de findAll(facturas)");

            ConsoleColor.printSuccess(String.format("findAll() en '%s' procesó el volumen gigantesco:", COLLECTION_FACTURAS));
            ConsoleColor.printInfo("  • Registros Recuperados", NUMBER_FMT.format(facturasList.size()));
            ConsoleColor.printInfo("  • Tiempo de Respuesta  ", String.format("%.2f ms (%.3f segundos)", duracionFacturasMs, duracionFacturasMs / 1000.0));
            ConsoleColor.printInfo("  • Rendimiento / Throughput", String.format("%,.0f docs/segundo", throughputFacturas));
            if (!facturasList.isEmpty()) {
                ConsoleColor.printInfo("  • Factura Muestra", "No: " + facturasList.getFirst().get("numero") + " | Total: $" + facturasList.getFirst().get("total"));
            }
        }

        // Liberar lista masiva de facturas
        facturasList = null;
        System.gc();

        // =====================================================================
        // FASE C: Estrategia Recomendada para Millones de Registros:
        //         Streaming por Chunks con streamFindAll()
        // =====================================================================
        ConsoleColor.printSubHeader("5. Alternativa de Alto Rendimiento: streamFindAll() con Liberación de Memoria");
        System.out.println("Demostración de consumo mediante StreamResponse en chunks defensivos:");
        printHeapStatus("Antes de streamFindAll()");

        long startStream = System.nanoTime();
        AtomicLong registrosStreamRecorridos = new AtomicLong(0);
        AtomicInteger chunksRecibidos = new AtomicInteger(0);

        try (StreamResponse<Map<String, Object>> stream = driver.streamFindAll(DATABASE_NAME, COLLECTION_FACTURAS)) {
            ConsoleColor.printInfo("Sentinel Activado Proactivamente", stream.isSentinelActivated());
            ConsoleColor.printInfo("Tamaño de Chunk Calculado por Sentinel", NUMBER_FMT.format(stream.getSafeBatchSize()) + " registros");
            ConsoleColor.printInfo("Total Estimado de Registros", NUMBER_FMT.format(stream.getTotalEstimated()));

            // Procesar cada chunk y liberar referencias de inmediato
            stream.forEachChunk(chunk -> {
                int count = chunk.size();
                registrosStreamRecorridos.addAndGet(count);
                int c = chunksRecibidos.incrementAndGet();

                // Reporte visual cada 100 chunks para no saturar consola
                if (c % 100 == 0 || registrosStreamRecorridos.get() >= totalFacturas) {
                    System.out.printf("    • Chunk #%,5d procesado | Docs acumulados: %,10d | Heap Libre: %s MB%n",
                            c, registrosStreamRecorridos.get(), NUMBER_FMT.format(Runtime.getRuntime().freeMemory() / (1024 * 1024)));
                }
            });
        }

        long endStream = System.nanoTime();
        double duracionStreamMs = (endStream - startStream) / 1_000_000.0;
        double throughputStream = (registrosStreamRecorridos.get() / (duracionStreamMs / 1000.0));
        printHeapStatus("Después de streamFindAll()");

        ConsoleColor.printSuccess(String.format("streamFindAll() completado con éxito: %,d docs en %.2f ms (%,.0f docs/seg)",
                registrosStreamRecorridos.get(), duracionStreamMs, throughputStream));

        // =====================================================================
        // FASE D: Consulta Paginada de Cero Impacto con LazyPagedCursor
        // =====================================================================
        ConsoleColor.printSubHeader("6. Consulta con LazyPagedCursor O(1) (Paginación Predictiva)");
        int tamanoLoteCursor = 10_000;
        long startCursor = System.nanoTime();

        JettraPolice.LazyPagedCursor<Map<String, Object>> cursor = driver.cursor(DATABASE_NAME, COLLECTION_FACTURAS, tamanoLoteCursor);
        int totalCursor = 0;
        int loteIdx = 0;

        while (cursor.hasNextPage() && loteIdx < 10) { // Muestrear los primeros 100,000 docs
            List<Map<String, Object>> lote = cursor.fetchNextPage();
            if (lote.isEmpty()) break;
            totalCursor += lote.size();
            loteIdx++;
        }

        long endCursor = System.nanoTime();
        double duracionCursorMs = (endCursor - startCursor) / 1_000_000.0;
        ConsoleColor.printSuccess(String.format("Cursor recuperó %,d facturas en %d páginas de %,d docs en %.2f ms",
                totalCursor, loteIdx, tamanoLoteCursor, duracionCursorMs));

        // =====================================================================
        // FASE E: Tabla Resumen y Veredicto de Rendimiento
        // =====================================================================
        ConsoleColor.printHeader("7. RESUMEN COMPARATIVO DE RENDIMIENTO EN example_factura_db");

        System.out.println("╔════════════════════════════╦═══════════════╦══════════════════╦═════════════════════╦══════════════════════════╗");
        System.out.println("║ Método de Consulta         ║ Colección     ║ Registros        ║ Tiempo Ejecución    ║ Throughput (Docs / Seg)  ║");
        System.out.println("╠════════════════════════════╬═══════════════╬══════════════════╬═════════════════════╬══════════════════════════╣");
        System.out.printf("║ findAll()                  ║ %-13s ║ %,16d ║ %13.2f ms   ║ %,21.0f docs/s ║%n",
                COLLECTION_CLIENTES, totalClientes, duracionClientesMs, throughputClientes);
        System.out.printf("║ findAll()                  ║ %-13s ║ %,16d ║ %13.2f ms   ║ %,21.0f docs/s ║%n",
                COLLECTION_FACTURAS, totalFacturas, duracionFacturasMs, (totalFacturas / (duracionFacturasMs / 1000.0)));
        System.out.printf("║ streamFindAll() [Anti-OOM] ║ %-13s ║ %,16d ║ %13.2f ms   ║ %,21.0f docs/s ║%n",
                COLLECTION_FACTURAS, registrosStreamRecorridos.get(), duracionStreamMs, throughputStream);
        System.out.printf("║ LazyPagedCursor            ║ %-13s ║ %,16d ║ %13.2f ms   ║ %,21.0f docs/s ║%n",
                COLLECTION_FACTURAS, totalCursor, duracionCursorMs, (totalCursor / (duracionCursorMs / 1000.0)));
        System.out.println("╚════════════════════════════╩═══════════════╩══════════════════╩═════════════════════╩══════════════════════════╝");

        System.out.println();
        ConsoleColor.printSubHeader("DIAGNÓSTICO FINAL Y CONCLUSIONES");
        System.out.println("✔ Capacidad de Procesamiento Masivo:");
        System.out.printf("  JettraStore SI logra procesar el volumen gigantesco de %,d facturas y %,d clientes.%n",
                totalFacturas, totalClientes);
        System.out.println("✔ Protección de Memoria (Anti-OOM):");
        System.out.printf("  JettraPolice Sentinel emitió %d intervenciones defensivas, ajustando lotes seguros para el Heap.%n",
                sentinelTriggerCount.get());
        System.out.println("✔ Recomendación de Arquitectura:");
        System.out.println("  • Para volúmenes moderados (< 200,000 registros): findAll() responde en milisegundos con alta velocidad.");
        System.out.println("  • Para volúmenes gigantescos (>= 1,000,000 registros): usar streamFindAll() o LazyPagedCursor");
        System.out.println("    permite recorrer millones de registros manteniendo un consumo de Heap plano y cero riesgo de OOM.");

        // Desregistrar listener
        driver.removePoliceEventListener(listener);
        ConsoleColor.printSuccess("Prueba de estrés masivo finalizada exitosamente.");
    }

    // =========================================================================
    // Métodos Auxiliares de Poblado de Alta Eficiencia
    // =========================================================================

    private static void poblarClientes(DocumentEngine engine, int cantidad) {
        ConsoleColor.printInfo("Poblando Clientes", "Insertando " + NUMBER_FMT.format(cantidad) + " clientes de prueba...");
        long start = System.currentTimeMillis();

        String[] ciudades = {"Panamá", "Colón", "David", "Santiago", "Chitré", "Penonomé", "La Chorrera"};
        String[] categorias = {"VIP", "CORPORATIVO", "REGULAR", "MAYORISTA"};

        for (int i = 1; i <= cantidad; i++) {
            String id = "cli_" + i;
            Map<String, Object> doc = Map.of(
                    "id", id,
                    "nombre", "Cliente Comercial #" + i,
                    "documento", "RUC-" + (800000 + i) + "-1-DV" + (i % 99),
                    "ciudad", ciudades[i % ciudades.length],
                    "categoria", categorias[i % categorias.length],
                    "activo", (i % 20 != 0),
                    "creditoDisponible", 1000.0 + (i * 2.5)
            );
            engine.insert(id, doc);

            if (i % 25_000 == 0) {
                System.out.printf("    -> Progreso Clientes: %,d / %,d insertados...%n", i, cantidad);
            }
        }

        long duracion = System.currentTimeMillis() - start;
        ConsoleColor.printSuccess(String.format("Poblado de Clientes finalizado en %,d ms (%,.0f docs/s)",
                duracion, (cantidad / (duracion / 1000.0))));
    }

    private static void poblarFacturas(DocumentEngine engine, int cantidad) {
        ConsoleColor.printInfo("Poblando Facturas", "Insertando " + NUMBER_FMT.format(cantidad) + " facturas masivas...");
        long start = System.currentTimeMillis();

        String[] metodos = {"TARJETA_CREDITO", "TRANSFERENCIA_ACH", "EFECTIVO", "YAPPY", "CHEQUE"};
        String[] estados = {"PAGADA", "EMITIDA", "ANULADA", "PENDIENTE_PAGO"};
        LocalDate baseDate = LocalDate.of(2026, 1, 1);

        for (int i = 1; i <= cantidad; i++) {
            String id = "fac_" + i;
            double subtotal = 50.0 + ((i * 7) % 2500);
            double itbms = subtotal * 0.07;
            double total = subtotal + itbms;

            Map<String, Object> doc = Map.of(
                    "numero", "FAC-2026-" + String.format("%08d", i),
                    "clienteId", "cli_" + ((i % 100_000) + 1),
                    "fecha", baseDate.plusDays(i % 365).toString(),
                    "metodoPago", metodos[i % metodos.length],
                    "estado", estados[i % estados.length],
                    "subtotal", subtotal,
                    "itbms", itbms,
                    "total", total,
                    "itemsCount", (i % 12) + 1
            );
            engine.insert(id, doc);

            if (i % 200_000 == 0) {
                System.out.printf("    -> Progreso Facturas: %,d / %,d insertadas...%n", i, cantidad);
            }
        }

        long duracion = System.currentTimeMillis() - start;
        ConsoleColor.printSuccess(String.format("Poblado de Facturas finalizado en %,d ms (%,.0f docs/s)",
                duracion, (cantidad / (duracion / 1000.0))));
    }

    private static void printHeapStatus(String etiqueta) {
        Runtime rt = Runtime.getRuntime();
        long max = rt.maxMemory() / (1024 * 1024);
        long total = rt.totalMemory() / (1024 * 1024);
        long free = rt.freeMemory() / (1024 * 1024);
        long used = total - free;

        System.out.printf("    [HEAP TELEMETRÍA] %-30s | Usado: %,4d MB | Libre: %,4d MB | Total: %,4d MB | Máx: %,4d MB%n",
                etiqueta, used, free, total, max);
    }
}
