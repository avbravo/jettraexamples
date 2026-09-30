package io.jettra.examples.store.driver;

import com.jettra.memory.api.JettraMemoryEngine;
import com.jettra.memory.engine.StorageMetrics;
import io.jettra.driver.JettraClient;
import io.jettra.driver.config.JettraClientConfig;
import io.jettra.store.core.JettraDatabase;
import io.jettra.store.core.StorageMode;
import io.jettra.store.engine.models.*;
import io.jettra.store.engine.query.JettraQLProcessor;
import io.jettra.store.engine.query.JettraSQLProcessor;
import io.jettra.store.police.JettraPolice;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Contrato de alto nivel para el controlador de acceso a JettraStore.
 * Integra de forma nativa soporte para todos los motores multimodelo
 * (Documentos, KeyValue, Vectorial, Grafos, Series Temporales, Geoespacial,
 * Columnar, Records y Off-Heap FFM Panama con JettraMemory), permitiendo
 * alternar dinámicamente entre almacenamiento en RAM (JVM_RAM) y DISK_MEMORY.
 */
public interface JettraDriver extends AutoCloseable {

    /**
     * Obtiene el cliente nativo JettraClient subyacente.
     */
    JettraClient getClient();

    /**
     * Obtiene la base de datos especificada, creándola si no existe con su configuración actual.
     */
    JettraDatabase getDatabase(String dbName);

    /**
     * Obtiene o inicializa la base de datos configurando explícitamente su StorageMode (RAM o DISK_MEMORY).
     */
    JettraDatabase getDatabase(String dbName, StorageMode mode);

    /**
     * Configura el modo de almacenamiento de una base de datos (JVM_RAM o DISK_MEMORY).
     */
    void setStorageMode(String dbName, StorageMode mode);

    /**
     * Obtiene el modo de almacenamiento activo para la base de datos.
     */
    StorageMode getStorageMode(String dbName);

    // =========================================================================
    // Motores de Almacenamiento (Engines)
    // =========================================================================

    /**
     * Motor NoSQL de Documentos tipo JSON / Map con control de concurrencia y vistas perezosas.
     */
    DocumentEngine getDocumentEngine(String dbName, String collection);

    /**
     * Motor Clave-Valor en memoria para acceso binario O(1) de ultra-baja latencia.
     */
    KeyValueEngine getKeyValueEngine(String dbName, String namespace);

    /**
     * Motor Vectorial para embeddings y búsqueda por similitud de coseno en IA.
     */
    VectorEngine getVectorEngine(String dbName, String collection, int dimensions);

    /**
     * Motor de Grafos con soporte para vértices, aristas dirigidas y propiedades.
     */
    GraphEngine getGraphEngine(String dbName, String graphName);

    /**
     * Motor de Series Temporales optimizado para telemetría, rangos de tiempo y promedios.
     */
    TimeSeriesEngine getTimeSeriesEngine(String dbName, String metric);

    /**
     * Motor Geoespacial con cálculo geodésico Haversine y búsqueda por radio en Km.
     */
    GeospatialEngine getGeospatialEngine(String dbName, String layer);

    /**
     * Motor Columnar para analítica OLAP en memoria con agregaciones vectorizadas.
     */
    ColumnarEngine getColumnarEngine(String dbName, String table);

    /**
     * Motor tipado para Java 25 Records con persistencia orientada a tipos inmutables.
     */
    <T extends Record> RecordsEngine<T> getRecordsEngine(String dbName, String entity, Class<T> recordClass);

    /**
     * Motor nativo JettraMemory para almacenamiento Off-Heap Panama LSM sin pausas de GC.
     */
    JettraMemoryEngine getMemoryEngine(String dbName);

    // =========================================================================
    // Operaciones Off-Heap Binarias Directas (Panama FFM)
    // =========================================================================

    void putBinary(String dbName, String key, byte[] data) throws IOException;

    byte[] getBinary(String dbName, String key) throws IOException;

    StorageMetrics getMemoryMetrics(String dbName);

    boolean compactMemory(String dbName) throws Exception;

    // =========================================================================
    // Consultas SQL, JQL y Paginación
    // =========================================================================

    JettraSQLProcessor.QueryResult sql(String dbName, String query);

    JettraSQLProcessor.QueryResult sqlPaged(String dbName, String query, int page, int pageSize);

    JettraPolice.LazyPagedCursor<Map<String, Object>> cursor(String dbName, String collection, int pageSize);

    JettraQLProcessor.JQLResult jql(String dbName, String query);

    // =========================================================================
    // Gestión del Catálogo
    // =========================================================================

    List<String> listDatabases();

    boolean dropDatabase(String dbName);

    boolean databaseExists(String dbName);

    @Override
    void close();

    // =========================================================================
    // Métodos Factory de Conexión
    // =========================================================================

    static JettraDriver open() {
        return new JettraDriverImpl();
    }

    static JettraDriver connect(String host, int port, String user, String pass) {
        return new JettraDriverImpl(host, port, user, pass);
    }

    static JettraDriver connect(JettraClientConfig config) {
        return new JettraDriverImpl(config);
    }
}
