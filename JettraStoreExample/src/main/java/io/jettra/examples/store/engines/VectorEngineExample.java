package io.jettra.examples.store.engines;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.core.StorageMode;
import io.jettra.store.engine.models.VectorEngine;

import java.util.List;

/**
 * Ejemplo completo del Motor Vectorial (VectorEngine).
 * Diseñado para indexación de embeddings multidimensionales y búsqueda
 * de máxima similitud semántica mediante Coseno (Cosine Similarity) para IA y RAG.
 */
public class VectorEngineExample {

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("3. MOTOR VECTORIAL (VectorEngine - Embeddings IA)");

        // 1. Base de datos Vectorial en RAM
        String ramDb = "ai_vectors_ram_db";
        driver.getDatabase(ramDb, StorageMode.JVM_RAM);
        ConsoleColor.printSubHeader("Ejecución en modo RAM (StorageMode.JVM_RAM)");
        testVectorEngine(driver, ramDb, "document_embeddings", 4);

        // 2. Base de datos Vectorial en DISK_MEMORY
        String diskDb = "ai_vectors_disk_db";
        driver.getDatabase(diskDb, StorageMode.DISK_MEMORY);
        ConsoleColor.printSubHeader("Ejecución en modo DISK_MEMORY (StorageMode.DISK_MEMORY)");
        testVectorEngine(driver, diskDb, "product_embeddings", 4);
    }

    private static void testVectorEngine(JettraDriver driver, String dbName, String collection, int dimensions) {
        VectorEngine engine = driver.getVectorEngine(dbName, collection, dimensions);

        ConsoleColor.printInfo("Base de datos", dbName + " [Modo: " + driver.getStorageMode(dbName) + "]");
        ConsoleColor.printInfo("Colección Vectorial", collection + " (" + dimensions + " Dimensiones)");

        // Indexación de vectores de muestra
        // Representando categorías temáticas:
        // [0: Inteligencia Artificial, 1: Computación en la Nube, 2: Bases de Datos, 3: Ciberseguridad]
        engine.index("art_ia_gen", new float[]{0.95f, 0.20f, 0.40f, 0.15f});
        engine.index("art_virtual_threads", new float[]{0.30f, 0.85f, 0.60f, 0.10f});
        engine.index("art_jettra_storage", new float[]{0.25f, 0.70f, 0.95f, 0.30f});
        engine.index("art_zero_trust_sec", new float[]{0.10f, 0.40f, 0.30f, 0.95f});

        ConsoleColor.printSuccess("4 vectores temáticos indexados correctamente.");
        ConsoleColor.printInfo("Total de vectores en índice", engine.size());

        // Vector de consulta (Target query): Interés enfocado fuertemente en Bases de Datos y Cloud
        float[] queryEmbedding = new float[]{0.20f, 0.75f, 0.90f, 0.25f};

        ConsoleColor.printInfo("Búsqueda Semántica de Máxima Similitud (Top 3 Coseno)", "");
        List<VectorEngine.VectorMatch> results = engine.searchCosine(queryEmbedding, 3);

        for (int i = 0; i < results.size(); i++) {
            VectorEngine.VectorMatch match = results.get(i);
            System.out.printf("    #%d -> Vector ID: %-20s | Similitud Coseno: %.4f%n",
                    (i + 1), match.id(), match.score());
        }

        if (!results.isEmpty()) {
            ConsoleColor.printSuccess("El resultado más relevante es: '" + results.getFirst().id() +
                    "' con score " + String.format("%.4f", results.getFirst().score()));
        }
    }
}
