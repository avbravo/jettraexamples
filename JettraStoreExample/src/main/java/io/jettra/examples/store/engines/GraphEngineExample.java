package io.jettra.examples.store.engines;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.core.StorageMode;
import io.jettra.store.engine.models.GraphEngine;

import java.util.List;
import java.util.Map;

/**
 * Ejemplo completo del Motor de Grafos (GraphEngine).
 * Diseñado para modelar topologías de red, relaciones sociales, grafos de
 * conocimiento y dependencias con aristas dirigidas tipadas y propiedades clave-valor.
 */
public class GraphEngineExample {

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("4. MOTOR DE GRAFOS (GraphEngine)");

        // 1. Grafo en memoria RAM
        String ramDb = "social_graph_ram_db";
        driver.getDatabase(ramDb, StorageMode.JVM_RAM);
        ConsoleColor.printSubHeader("Ejecución en modo RAM (StorageMode.JVM_RAM)");
        testGraphEngine(driver, ramDb, "red_colaboradores");

        // 2. Grafo en DISK_MEMORY
        String diskDb = "infra_graph_disk_db";
        driver.getDatabase(diskDb, StorageMode.DISK_MEMORY);
        ConsoleColor.printSubHeader("Ejecución en modo DISK_MEMORY (StorageMode.DISK_MEMORY)");
        testGraphEngine(driver, diskDb, "topologia_datacenter");
    }

    private static void testGraphEngine(JettraDriver driver, String dbName, String graphName) {
        GraphEngine graph = driver.getGraphEngine(dbName, graphName);

        ConsoleColor.printInfo("Base de datos", dbName + " [Modo: " + driver.getStorageMode(dbName) + "]");
        ConsoleColor.printInfo("Nombre del Grafo", graphName);

        // Creación de vértices (Nodos)
        graph.addVertex("cluster_master");
        graph.addVertex("node_worker_01");
        graph.addVertex("node_worker_02");
        graph.addVertex("storage_san_01");

        // Creación de aristas dirigidas (Edges) con etiquetas y propiedades de conexión
        graph.addEdge("cluster_master", "node_worker_01", "MANAGES",
                Map.of("latency_ms", 0.45, "protocol", "GRPC", "bandwidth_gbps", 25));

        graph.addEdge("cluster_master", "node_worker_02", "MANAGES",
                Map.of("latency_ms", 0.52, "protocol", "GRPC", "bandwidth_gbps", 25));

        graph.addEdge("node_worker_01", "storage_san_01", "WRITES_TO",
                Map.of("iops", 150000, "interface", "NVMe-oF", "encrypted", true));

        graph.addEdge("node_worker_02", "storage_san_01", "WRITES_TO",
                Map.of("iops", 120000, "interface", "NVMe-oF", "encrypted", true));

        ConsoleColor.printSuccess("Vértices y Aristas creados en el grafo.");
        ConsoleColor.printInfo("Total de Vértices en Grafo", graph.size());
        ConsoleColor.printInfo("Vértices registrados", graph.getVertices());

        // Recorrido de Aristas Salientes (Outbound Edges) desde 'cluster_master'
        ConsoleColor.printInfo("Relaciones salientes desde 'cluster_master'", "");
        List<GraphEngine.Edge> masterEdges = graph.getOutboundEdges("cluster_master");
        for (GraphEngine.Edge edge : masterEdges) {
            System.out.printf("    ──[%s]──> Destino: %s | Propiedades: %s%n",
                    edge.label(), edge.targetVertex(), edge.properties());
        }

        // Recorrido de Aristas Salientes desde 'node_worker_01'
        ConsoleColor.printInfo("Relaciones salientes desde 'node_worker_01'", "");
        List<GraphEngine.Edge> workerEdges = graph.getOutboundEdges("node_worker_01");
        for (GraphEngine.Edge edge : workerEdges) {
            System.out.printf("    ──[%s]──> Destino: %s | Propiedades: %s%n",
                    edge.label(), edge.targetVertex(), edge.properties());
        }
    }
}
