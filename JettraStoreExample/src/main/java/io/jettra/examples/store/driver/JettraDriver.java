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
    // Agregaciones, Matemáticas, Finanzas, Estadística y Álgebra Vectorial
    // =========================================================================

    // Agregaciones
    io.jettra.store.calc.JettraAggregation.AggregationResult aggregate(
        String dbName, String collection, List<String> groupByFields, List<io.jettra.store.calc.JettraAggregation.AggregateSpec> specs);
    io.jettra.store.calc.JettraAggregation.AggregationResult aggregateSum(String dbName, String collection, String field, String groupBy);
    io.jettra.store.calc.JettraAggregation.AggregationResult aggregateAvg(String dbName, String collection, String field, String groupBy);
    io.jettra.store.calc.JettraAggregation.AggregationResult aggregateMin(String dbName, String collection, String field, String groupBy);
    io.jettra.store.calc.JettraAggregation.AggregationResult aggregateMax(String dbName, String collection, String field, String groupBy);
    io.jettra.store.calc.JettraAggregation.AggregationResult aggregateCount(String dbName, String collection, String groupBy);
    io.jettra.store.calc.JettraAggregation.AggregationResult aggregateMedian(String dbName, String collection, String field, String groupBy);

    // Matemáticas
    double evalMath(String expression);
    double sqrt(double x);
    double cbrt(double x);
    double pow(double b, double e);
    double round(double x, int decimals);
    long factorial(int n);
    long gcd(long a, long b);
    long lcm(long a, long b);
    double hypot(double x, double y);

    // Finanzas
    double pmt(double rate, int nper, double pv);
    double fv(double rate, int nper, double pmt, double pv);
    double pv(double rate, int nper, double pmt, double fv);
    double cagr(double beginningValue, double endingValue, double periods);
    double compoundInterest(double principal, double annualRate, int compoundsPerYear, double years);
    double simpleInterest(double principal, double annualRate, double years);
    List<io.jettra.store.calc.JettraFinance.AmortizationRow> amortizationSchedule(double principal, double annualRate, int periods);
    double npv(double rate, double... cashFlows);
    double irr(double... cashFlows);

    // Estadística
    double statsMean(List<? extends Number> data);
    double statsMedian(List<? extends Number> data);
    double statsStdDev(List<? extends Number> data);
    double statsVariance(List<? extends Number> data);
    double statsIqr(List<? extends Number> data);
    io.jettra.store.calc.JettraStatistics.StatsSummary statsSummary(List<? extends Number> data);
    double statsCorrelation(List<? extends Number> x, List<? extends Number> y);
    io.jettra.store.calc.JettraStatistics.RegressionResult statsLinearRegression(List<? extends Number> x, List<? extends Number> y);

    // Álgebra Vectorial
    float dotProduct(float[] v1, float[] v2);
    float cosineSimilarity(float[] v1, float[] v2);
    float euclideanDistance(float[] v1, float[] v2);
    float manhattanDistance(float[] v1, float[] v2);
    float chebyshevDistance(float[] v1, float[] v2);
    float[] crossProduct(float[] v1, float[] v2);
    double vectorAngleDegrees(float[] v1, float[] v2);
    float[] vectorNormalize(float[] v);
    float[] vectorAdd(float[] v1, float[] v2);
    float[] vectorSubtract(float[] v1, float[] v2);
    float[] vectorCentroid(List<float[]> vectors);

    // =========================================================================
    // Gestión del Catálogo
    // =========================================================================

    // =========================================================================
    // Streaming por Chunks y Listener Anti-OOM de JettraPolice Sentinel
    // =========================================================================

    void addPoliceEventListener(io.jettra.driver.listener.JettraPoliceEventListener listener);

    void removePoliceEventListener(io.jettra.driver.listener.JettraPoliceEventListener listener);

    io.jettra.store.core.StreamResponse<Map<String, Object>> streamFindAll(String dbName, String collection);

    io.jettra.store.core.StreamResponse<Map<String, Object>> streamFindAll(String dbName, String collection, int limit);

    List<Map<String, Object>> findAll(String dbName, String collection);

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
