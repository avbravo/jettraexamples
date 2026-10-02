package io.jettra.examples.store;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.engines.*;
import io.jettra.examples.store.query.SqlAndCursorExample;
import io.jettra.examples.store.storage.StorageModeComparisonExample;
import io.jettra.examples.store.util.ConsoleColor;

/**
 * Aplicación principal de demostración de JettraStore y JettraDriver en Java 25.
 * Ejecuta paso a paso ejemplos con cada motor de almacenamiento multimodelo
 * utilizando bases de datos en memoria RAM (JVM_RAM) y en DISK_MEMORY.
 */
public class JettraStoreExampleApp {

    public static void main(String[] args) {
        ConsoleColor.printHeader("JettraStoreExample - DEMO MULTIMODELO EN JAVA 25 CON JETTRADRIVER");
        System.out.println("Plataforma: Java " + System.getProperty("java.version") + " (" + System.getProperty("os.name") + ")");
        System.out.println("Virtual Threads: Habilitados | Zero-Boxing: Activo");

        long startTotal = System.currentTimeMillis();

        try (JettraDriver driver = JettraDriver.open()) {

            // 1. Motor de Documentos (JSON / NoSQL)
            DocumentEngineExample.run(driver);

            // 2. Motor Clave-Valor (KeyValue)
            KeyValueEngineExample.run(driver);

            // 3. Motor Vectorial (Embeddings y Similitud Coseno)
            VectorEngineExample.run(driver);

            // 4. Motor de Grafos (Vértices y Aristas dirigidas)
            GraphEngineExample.run(driver);

            // 5. Motor de Series Temporales (Métricas y Rangos)
            TimeSeriesEngineExample.run(driver);

            // 6. Motor Geoespacial (Coordenadas y Radio Haversine)
            GeospatialEngineExample.run(driver);

            // 7. Motor Columnar (Analítica OLAP y Agregaciones)
            ColumnarEngineExample.run(driver);

            // 8. Motor Pure Object / Records (Java 25 Records)
            RecordsEngineExample.run(driver);

            // 9. Motor Off-Heap JettraMemory (Buffers FFM Panama)
            MemoryOffHeapExample.run(driver);

            // 10. Comparativa RAM (JVM_RAM) vs DISK_MEMORY
            StorageModeComparisonExample.run(driver);

            // 11. Consultas SQL, Paginación Predictiva y LazyPagedCursor
            SqlAndCursorExample.run(driver);

            // 12. Analítica Avanzada, Agregaciones, Matemáticas, Finanzas y Vectores
            io.jettra.examples.store.calc.AnalyticsAndCalcExample.run(driver);

            // 13. Prevención Anti-OOM: Sentinel, Streaming por Chunks y Listeners
            io.jettra.examples.store.police.AntiOomStreamingSentinelExample.run(driver);

            // 14. Consultas Masivas findAll() sobre example_factura_db (Millones de Registros)
            io.jettra.examples.store.query.MassiveFindAllMillionRecordsExample.run(driver);

            // Catálogo final de bases de datos registradas
            ConsoleColor.printHeader("RESUMEN DEL CATÁLOGO DE BASES DE DATOS");
            var dbs = driver.listDatabases();
            ConsoleColor.printInfo("Total de Bases de Datos Gestionadas", dbs.size());
            for (String db : dbs) {
                System.out.printf("  • %-32s -> Modo: %s%n", db, driver.getStorageMode(db));
            }

            long totalDuration = System.currentTimeMillis() - startTotal;
            ConsoleColor.printHeader("TODOS LOS EJEMPLOS SE EJECUTARON EXITOSAMENTE EN " + totalDuration + " ms");

        } catch (Exception ex) {
            System.err.println(ConsoleColor.RED + "Error durante la ejecución del ejemplo: " + ex.getMessage() + ConsoleColor.RESET);
            ex.printStackTrace();
        }
    }
}
