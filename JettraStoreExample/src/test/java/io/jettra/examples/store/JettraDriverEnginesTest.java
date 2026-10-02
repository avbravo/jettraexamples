package io.jettra.examples.store;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.model.ProductRecord;
import io.jettra.store.core.StorageMode;
import io.jettra.store.engine.models.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias integradas para validar que JettraDriver y cada uno
 * de los motores funcionen correctamente tanto en RAM como en DISK_MEMORY.
 */
class JettraDriverEnginesTest {

    private static JettraDriver driver;

    @BeforeAll
    static void setup() {
        driver = JettraDriver.open();
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.close();
        }
    }

    @Test
    void testStorageModeConfiguration() {
        String dbName = "test_mode_db";
        driver.getDatabase(dbName, StorageMode.JVM_RAM);
        assertEquals(StorageMode.JVM_RAM, driver.getStorageMode(dbName));

        driver.setStorageMode(dbName, StorageMode.DISK_MEMORY);
        assertEquals(StorageMode.DISK_MEMORY, driver.getStorageMode(dbName));
    }

    @Test
    void testDocumentEngine() {
        String dbName = "test_doc_db";
        driver.getDatabase(dbName, StorageMode.JVM_RAM);
        DocumentEngine engine = driver.getDocumentEngine(dbName, "users");

        engine.insert("u1", Map.of("name", "Alice", "role", "ADMIN"));
        engine.insert("u2", Map.of("name", "Bob", "role", "USER"));

        assertEquals(2, engine.count());
        Map<String, Object> u1 = engine.findById("u1");
        assertNotNull(u1);
        assertEquals("Alice", u1.get("name"));
    }

    @Test
    void testKeyValueEngine() {
        String dbName = "test_kv_db";
        driver.getDatabase(dbName, StorageMode.DISK_MEMORY);
        KeyValueEngine kv = driver.getKeyValueEngine(dbName, "cache");

        kv.put("k1", "Hello Jettra".getBytes(StandardCharsets.UTF_8));
        assertTrue(kv.containsKey("k1"));

        byte[] val = kv.get("k1");
        assertNotNull(val);
        assertEquals("Hello Jettra", new String(val, StandardCharsets.UTF_8));
    }

    @Test
    void testVectorEngine() {
        String dbName = "test_vec_db";
        driver.getDatabase(dbName, StorageMode.JVM_RAM);
        VectorEngine engine = driver.getVectorEngine(dbName, "embeddings", 3);

        engine.index("v1", new float[]{1.0f, 0.0f, 0.0f});
        engine.index("v2", new float[]{0.0f, 1.0f, 0.0f});

        List<VectorEngine.VectorMatch> matches = engine.searchCosine(new float[]{0.95f, 0.05f, 0.0f}, 1);
        assertFalse(matches.isEmpty());
        assertEquals("v1", matches.getFirst().id());
    }

    @Test
    void testGraphEngine() {
        String dbName = "test_graph_db";
        driver.getDatabase(dbName, StorageMode.JVM_RAM);
        GraphEngine graph = driver.getGraphEngine(dbName, "network");

        graph.addVertex("nodeA");
        graph.addVertex("nodeB");
        graph.addEdge("nodeA", "nodeB", "CONNECTS", Map.of("weight", 10));

        assertEquals(2, graph.size());
        List<GraphEngine.Edge> outbound = graph.getOutboundEdges("nodeA");
        assertEquals(1, outbound.size());
        assertEquals("nodeB", outbound.getFirst().targetVertex());
    }

    @Test
    void testTimeSeriesEngine() {
        String dbName = "test_ts_db";
        driver.getDatabase(dbName, StorageMode.DISK_MEMORY);
        TimeSeriesEngine ts = driver.getTimeSeriesEngine(dbName, "temperature");

        ts.record(1000L, 20.0);
        ts.record(2000L, 24.0);
        ts.record(3000L, 28.0);

        assertEquals(3, ts.size());
        assertEquals(24.0, ts.average(1000L, 3000L), 0.001);
    }

    @Test
    void testGeospatialEngine() {
        String dbName = "test_geo_db";
        driver.getDatabase(dbName, StorageMode.JVM_RAM);
        GeospatialEngine geo = driver.getGeospatialEngine(dbName, "landmarks");

        geo.insertPoint("origin", 8.9824, -79.5199);
        geo.insertPoint("nearby", 8.9900, -79.5200);
        geo.insertPoint("far", 10.0000, -80.0000);

        var results = geo.findWithinRadius(8.9824, -79.5199, 5.0);
        assertEquals(2, results.size());
    }

    @Test
    void testColumnarEngine() {
        String dbName = "test_col_db";
        driver.getDatabase(dbName, StorageMode.DISK_MEMORY);
        ColumnarEngine col = driver.getColumnarEngine(dbName, "sales");

        col.appendRow(Map.of("amount", 100.0, "qty", 2));
        col.appendRow(Map.of("amount", 250.0, "qty", 3));

        assertEquals(2, col.getRowCount());
        assertEquals(350.0, col.sumColumn("amount"), 0.001);
        assertEquals(5.0, col.sumColumn("qty"), 0.001);
    }

    @Test
    void testRecordsEngine() {
        String dbName = "test_records_db";
        driver.getDatabase(dbName, StorageMode.JVM_RAM);
        RecordsEngine<ProductRecord> engine = driver.getRecordsEngine(dbName, "products", ProductRecord.class);

        ProductRecord p = new ProductRecord("SKU-1", "Servidor", "IT", new BigDecimal("1999.00"), 10, true);
        engine.persist(p.sku(), p);

        assertEquals(1, engine.size());
        ProductRecord found = engine.find("SKU-1");
        assertNotNull(found);
        assertEquals("Servidor", found.name());
    }

    @Test
    void testSqlAndCursor() {
        String dbName = "test_sql_db";
        driver.getDatabase(dbName, StorageMode.JVM_RAM);
        DocumentEngine engine = driver.getDocumentEngine(dbName, "items");

        for (int i = 1; i <= 10; i++) {
            engine.insert("item_" + i, Map.of("num", i, "type", "A"));
        }

        var res = driver.sql(dbName, "SELECT * FROM items");
        assertEquals(10, res.rows().size());

        var cursor = driver.cursor(dbName, "items", 4);
        assertTrue(cursor.hasNextPage());
        List<Map<String, Object>> batch = cursor.fetchNextPage();
        assertEquals(4, batch.size());
    }
    @Test
    void testAnalyticsAndCalculations() {
        String dbName = "test_calc_engines_db";
        driver.getDatabase(dbName, StorageMode.JVM_RAM);
        DocumentEngine sales = driver.getDocumentEngine(dbName, "orders");

        sales.insert("o1", Map.of("zone", "North", "val", 100.0));
        sales.insert("o2", Map.of("zone", "North", "val", 200.0));
        sales.insert("o3", Map.of("zone", "South", "val", 500.0));

        // 1. Aggregations
        var agg = driver.aggregateSum(dbName, "orders", "val", "zone");
        assertEquals(2, agg.totalGroups());

        // 2. Math
        assertEquals(10.0, driver.sqrt(100.0), 0.001);
        assertEquals(5.0, driver.hypot(3.0, 4.0), 0.001);
        assertEquals(720L, driver.factorial(6));

        // 3. Finance
        double pmt = driver.pmt(0.05 / 12.0, 360, 200000.0);
        assertTrue(pmt > 1000.0);
        double cagr = driver.cagr(100.0, 200.0, 3.0);
        assertTrue(cagr > 25.0);

        // 4. Statistics
        var nums = List.of(10.0, 20.0, 30.0, 40.0, 50.0);
        assertEquals(30.0, driver.statsMean(nums), 0.001);
        assertEquals(30.0, driver.statsMedian(nums), 0.001);

        // 5. Vectors
        float[] v1 = new float[]{1f, 0f, 0f};
        float[] v2 = new float[]{0f, 1f, 0f};
        assertEquals(0f, driver.dotProduct(v1, v2), 0.001f);
        float[] cross = driver.crossProduct(v1, v2);
        assertEquals(1f, cross[2], 0.001f);

        // 6. Run example end-to-end
        io.jettra.examples.store.calc.AnalyticsAndCalcExample.run(driver);
    }

    @Test
    void testAntiOomStreamingSentinelExample() {
        io.jettra.examples.store.police.AntiOomStreamingSentinelExample.run(driver);

        String dbName = "streaming_sentinel_db";
        try (var stream = driver.streamFindAll(dbName, "catalogo_masivo")) {
            assertNotNull(stream);
            assertTrue(stream.getSafeBatchSize() > 0);
            assertTrue(stream.isSentinelActivated());
            var all = stream.collectAll();
            assertEquals(stream.getSafeBatchSize(), all.size());
        }

        var list = driver.findAll(dbName, "catalogo_masivo");
        assertNotNull(list);
        assertEquals(100, list.size());
    }
}
