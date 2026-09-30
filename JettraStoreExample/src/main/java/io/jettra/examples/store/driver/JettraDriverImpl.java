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
