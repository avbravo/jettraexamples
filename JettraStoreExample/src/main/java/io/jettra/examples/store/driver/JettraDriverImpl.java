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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación de referencia del controlador JettraDriver para Java 25.
 * Proporciona un punto de entrada unificado y desacoplado para gestionar
 * bases de datos multimodelo con almacenamiento en RAM o DISK_MEMORY.
 */
public class JettraDriverImpl implements JettraDriver {

    private final JettraClient client;
    private final Map<String, Map<String, RecordsEngine<?>>> recordsEnginesRegistry = new ConcurrentHashMap<>();

    public JettraDriverImpl() {
        this("127.0.0.1", 9091, "admin", "admin-jettra");
    }

    public JettraDriverImpl(String host, int port, String user, String pass) {
        JettraClientConfig cfg = JettraClientConfig.builder()
                .addClusterNode(host, port)
                .credentials(user, pass)
                .enableVirtualThreads(true)
                .build();
        this.client = JettraClient.connect(cfg);
    }

    public JettraDriverImpl(JettraClientConfig config) {
        this.client = JettraClient.connect(config);
    }

    public JettraDriverImpl(JettraClient client) {
        this.client = client;
    }

    @Override
    public JettraClient getClient() {
        return client;
    }

    @Override
    public JettraDatabase getDatabase(String dbName) {
        return client.getDatabase(dbName);
    }

    @Override
    public JettraDatabase getDatabase(String dbName, StorageMode mode) {
        JettraDatabase db = client.getDatabase(dbName);
        if (mode != null) {
            db.setStorageMode(mode);
        }
        return db;
    }

    @Override
    public void setStorageMode(String dbName, StorageMode mode) {
        client.setStorageMode(dbName, mode);
    }

    @Override
    public StorageMode getStorageMode(String dbName) {
        return client.getStorageMode(dbName);
    }

    @Override
    public DocumentEngine getDocumentEngine(String dbName, String collection) {
        return getDatabase(dbName).getDocumentEngine(collection);
    }

    @Override
    public KeyValueEngine getKeyValueEngine(String dbName, String namespace) {
        return getDatabase(dbName).getKeyValueEngine(namespace);
    }

    @Override
    public VectorEngine getVectorEngine(String dbName, String collection, int dimensions) {
        return getDatabase(dbName).getVectorEngine(collection, dimensions);
    }

    @Override
    public GraphEngine getGraphEngine(String dbName, String graphName) {
        return getDatabase(dbName).getGraphEngine(graphName);
    }

    @Override
    public TimeSeriesEngine getTimeSeriesEngine(String dbName, String metric) {
        return getDatabase(dbName).getTimeSeriesEngine(metric);
    }

    @Override
    public GeospatialEngine getGeospatialEngine(String dbName, String layer) {
        return getDatabase(dbName).getGeospatialEngine(layer);
    }

    @Override
    public ColumnarEngine getColumnarEngine(String dbName, String table) {
        return getDatabase(dbName).getColumnarEngine(table);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Record> RecordsEngine<T> getRecordsEngine(String dbName, String entity, Class<T> recordClass) {
        Map<String, RecordsEngine<?>> dbRecords = recordsEnginesRegistry.computeIfAbsent(dbName, k -> new ConcurrentHashMap<>());
        return (RecordsEngine<T>) dbRecords.computeIfAbsent(entity, k -> new RecordsEngine<>(entity, recordClass));
    }

    @Override
    public JettraMemoryEngine getMemoryEngine(String dbName) {
        return client.getMemoryEngine(dbName);
    }

    @Override
    public void putBinary(String dbName, String key, byte[] data) throws IOException {
        client.putBinary(dbName, key, data);
    }

    @Override
    public byte[] getBinary(String dbName, String key) throws IOException {
        return client.getBinary(dbName, key);
    }

    @Override
    public StorageMetrics getMemoryMetrics(String dbName) {
        return client.getMemoryMetrics(dbName);
    }

    @Override
    public boolean compactMemory(String dbName) throws Exception {
        return client.compactMemory(dbName);
    }

    @Override
    public JettraSQLProcessor.QueryResult sql(String dbName, String query) {
        return client.sql(dbName, query);
    }

    @Override
    public JettraSQLProcessor.QueryResult sqlPaged(String dbName, String query, int page, int pageSize) {
        return client.sqlPaged(dbName, query, page, pageSize);
    }

    @Override
    public JettraPolice.LazyPagedCursor<Map<String, Object>> cursor(String dbName, String collection, int pageSize) {
        return client.cursor(dbName, collection, pageSize);
    }

    @Override
    public JettraQLProcessor.JQLResult jql(String dbName, String query) {
        return client.jql(dbName, query);
    }

    // =========================================================================
    // Agregaciones, Matemáticas, Finanzas, Estadística y Álgebra Vectorial
    // =========================================================================

    @Override
    public io.jettra.store.calc.JettraAggregation.AggregationResult aggregate(
            String dbName, String collection, List<String> groupByFields, List<io.jettra.store.calc.JettraAggregation.AggregateSpec> specs) {
        return client.aggregate(dbName, collection, groupByFields, specs);
    }

    @Override
    public io.jettra.store.calc.JettraAggregation.AggregationResult aggregateSum(String dbName, String collection, String field, String groupBy) {
        return client.aggregateSum(dbName, collection, field, groupBy);
    }

    @Override
    public io.jettra.store.calc.JettraAggregation.AggregationResult aggregateAvg(String dbName, String collection, String field, String groupBy) {
        return client.aggregateAvg(dbName, collection, field, groupBy);
    }

    @Override
    public io.jettra.store.calc.JettraAggregation.AggregationResult aggregateMin(String dbName, String collection, String field, String groupBy) {
        return client.aggregateMin(dbName, collection, field, groupBy);
    }

    @Override
    public io.jettra.store.calc.JettraAggregation.AggregationResult aggregateMax(String dbName, String collection, String field, String groupBy) {
        return client.aggregateMax(dbName, collection, field, groupBy);
    }

    @Override
    public io.jettra.store.calc.JettraAggregation.AggregationResult aggregateCount(String dbName, String collection, String groupBy) {
        return client.aggregateCount(dbName, collection, groupBy);
    }

    @Override
    public io.jettra.store.calc.JettraAggregation.AggregationResult aggregateMedian(String dbName, String collection, String field, String groupBy) {
        return client.aggregateMedian(dbName, collection, field, groupBy);
    }

    @Override
    public double evalMath(String expression) { return client.evalMath(expression); }
    @Override
    public double sqrt(double x) { return client.sqrt(x); }
    @Override
    public double cbrt(double x) { return client.cbrt(x); }
    @Override
    public double pow(double b, double e) { return client.pow(b, e); }
    @Override
    public double round(double x, int decimals) { return client.round(x, decimals); }
    @Override
    public long factorial(int n) { return client.factorial(n); }
    @Override
    public long gcd(long a, long b) { return client.gcd(a, b); }
    @Override
    public long lcm(long a, long b) { return client.lcm(a, b); }
    @Override
    public double hypot(double x, double y) { return client.hypot(x, y); }

    @Override
    public double pmt(double rate, int nper, double pv) { return client.pmt(rate, nper, pv); }
    @Override
    public double fv(double rate, int nper, double pmt, double pv) { return client.fv(rate, nper, pmt, pv); }
    @Override
    public double pv(double rate, int nper, double pmt, double fv) { return client.pv(rate, nper, pmt, fv); }
    @Override
    public double cagr(double beginningValue, double endingValue, double periods) { return client.cagr(beginningValue, endingValue, periods); }
    @Override
    public double compoundInterest(double principal, double annualRate, int compoundsPerYear, double years) {
        return client.compoundInterest(principal, annualRate, compoundsPerYear, years);
    }
    @Override
    public double simpleInterest(double principal, double annualRate, double years) {
        return client.simpleInterest(principal, annualRate, years);
    }
    @Override
    public List<io.jettra.store.calc.JettraFinance.AmortizationRow> amortizationSchedule(double principal, double annualRate, int periods) {
        return client.amortizationSchedule(principal, annualRate, periods);
    }
    @Override
    public double npv(double rate, double... cashFlows) { return client.npv(rate, cashFlows); }
    @Override
    public double irr(double... cashFlows) { return client.irr(cashFlows); }

    @Override
    public double statsMean(List<? extends Number> data) { return client.statsMean(data); }
    @Override
    public double statsMedian(List<? extends Number> data) { return client.statsMedian(data); }
    @Override
    public double statsStdDev(List<? extends Number> data) { return client.statsStdDev(data); }
    @Override
    public double statsVariance(List<? extends Number> data) { return client.statsVariance(data); }
    @Override
    public double statsIqr(List<? extends Number> data) { return client.statsIqr(data); }
    @Override
    public io.jettra.store.calc.JettraStatistics.StatsSummary statsSummary(List<? extends Number> data) { return client.statsSummary(data); }
    @Override
    public double statsCorrelation(List<? extends Number> x, List<? extends Number> y) { return client.statsCorrelation(x, y); }
    @Override
    public io.jettra.store.calc.JettraStatistics.RegressionResult statsLinearRegression(List<? extends Number> x, List<? extends Number> y) {
        return client.statsLinearRegression(x, y);
    }

    @Override
    public float dotProduct(float[] v1, float[] v2) { return client.dotProduct(v1, v2); }
    @Override
    public float cosineSimilarity(float[] v1, float[] v2) { return client.cosineSimilarity(v1, v2); }
    @Override
    public float euclideanDistance(float[] v1, float[] v2) { return client.euclideanDistance(v1, v2); }
    @Override
    public float manhattanDistance(float[] v1, float[] v2) { return client.manhattanDistance(v1, v2); }
    @Override
    public float chebyshevDistance(float[] v1, float[] v2) { return client.chebyshevDistance(v1, v2); }
    @Override
    public float[] crossProduct(float[] v1, float[] v2) { return client.crossProduct(v1, v2); }
    @Override
    public double vectorAngleDegrees(float[] v1, float[] v2) { return client.vectorAngleDegrees(v1, v2); }
    @Override
    public float[] vectorNormalize(float[] v) { return client.normalize(v); }
    @Override
    public float[] vectorAdd(float[] v1, float[] v2) { return client.vectorAdd(v1, v2); }
    @Override
    public float[] vectorSubtract(float[] v1, float[] v2) { return client.vectorSubtract(v1, v2); }
    @Override
    public float[] vectorCentroid(List<float[]> vectors) { return client.centroid(vectors); }

    // =========================================================================
    // Streaming por Chunks y Listener Anti-OOM de JettraPolice Sentinel
    // =========================================================================

    @Override
    public void addPoliceEventListener(io.jettra.driver.listener.JettraPoliceEventListener listener) {
        client.addPoliceEventListener(listener);
    }

    @Override
    public void removePoliceEventListener(io.jettra.driver.listener.JettraPoliceEventListener listener) {
        client.removePoliceEventListener(listener);
    }

    @Override
    public io.jettra.store.core.StreamResponse<Map<String, Object>> streamFindAll(String dbName, String collection) {
        return client.streamFindAll(dbName, collection);
    }

    @Override
    public io.jettra.store.core.StreamResponse<Map<String, Object>> streamFindAll(String dbName, String collection, int limit) {
        return client.streamFindAll(dbName, collection, limit);
    }

    @Override
    public List<Map<String, Object>> findAll(String dbName, String collection) {
        return client.findAll(dbName, collection);
    }

    @Override
    public List<String> listDatabases() {
        return client.listDatabases();
    }

    @Override
    public boolean dropDatabase(String dbName) {
        recordsEnginesRegistry.remove(dbName);
        return client.dropDatabase(dbName);
    }

    @Override
    public boolean databaseExists(String dbName) {
        return client.databaseExists(dbName);
    }

    @Override
    public void close() {
        recordsEnginesRegistry.clear();
        client.close();
    }
}
