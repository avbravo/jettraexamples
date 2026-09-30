package io.jettra.examples.store.query;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.core.StorageMode;
import io.jettra.store.engine.models.DocumentEngine;
import io.jettra.store.engine.query.JettraSQLProcessor;
import io.jettra.store.police.JettraPolice;

import java.util.List;
import java.util.Map;

/**
 * Ejemplo de Consultas JettraSQL, Paginación Predictiva y Cursores Perezosos (Lazy Cursor).
 * Diseñado para garantizar que las consultas analíticas o por lotes se ejecuten
 * con cero impacto en el Heap y sin riesgo de OutOfMemoryError.
 */
public class SqlAndCursorExample {

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("11. CONSULTAS SQL, PAGINACIÓN Y LAZY CURSOR");

        String dbName = "ventas_sql_db";
        driver.getDatabase(dbName, StorageMode.JVM_RAM);

        // Poblado de datos de prueba
        DocumentEngine clientes = driver.getDocumentEngine(dbName, "clientes");
        for (int i = 1; i <= 25; i++) {
            clientes.insert("cli_" + i, Map.of(
                    "id", i,
                    "nombre", "Cliente " + i,
                    "ciudad", (i % 3 == 0) ? "Panama" : ((i % 3 == 1) ? "Colon" : "David"),
                    "activo", (i % 5 != 0),
                    "balance", 100.0 * i
            ));
        }

        ConsoleColor.printSuccess("25 clientes registrados en la colección.");

        // 1. Consulta SQL tradicional
        ConsoleColor.printSubHeader("1. Consulta JettraSQL directa");
        String sql = "SELECT * FROM clientes WHERE ciudad = 'Panama'";
        ConsoleColor.printInfo("SQL", sql);

        JettraSQLProcessor.QueryResult res = driver.sql(dbName, sql);
        ConsoleColor.printSuccess("Filas encontradas: " + res.rows().size() + " (" + res.message() + ")");
        ConsoleColor.printInfo("Columnas proyectadas", res.columns());
        for (List<Object> row : res.rows().subList(0, Math.min(3, res.rows().size()))) {
            System.out.println("    -> Fila: " + row);
        }

        // 2. Consulta SQL Paginada Segura (sqlPaged)
        ConsoleColor.printSubHeader("2. Consulta SQL Paginada (sqlPaged)");
        int pagina = 2;
        int tamanoPagina = 5;
        ConsoleColor.printInfo("Solicitando", "Página " + pagina + " (Lote de " + tamanoPagina + " filas)");

        JettraSQLProcessor.QueryResult pagedRes = driver.sqlPaged(dbName, "SELECT * FROM clientes", pagina, tamanoPagina);
        ConsoleColor.printSuccess("Filas recuperadas en página: " + pagedRes.rows().size());
        for (List<Object> row : pagedRes.rows()) {
            System.out.printf("    [Pág %d] -> %s%n", pagina, row);
        }

        // 3. Procesamiento masivo O(1) con LazyPagedCursor
        ConsoleColor.printSubHeader("3. Streaming por lotes con LazyPagedCursor (Zero Heap Pressure)");
        JettraPolice.LazyPagedCursor<Map<String, Object>> cursor = driver.cursor(dbName, "clientes", 10);

        int totalIterado = 0;
        int loteIndex = 1;
        while (cursor.hasNextPage()) {
            List<Map<String, Object>> lote = cursor.fetchNextPage();
            if (lote.isEmpty()) break;

            totalIterado += lote.size();
            System.out.printf("    • Lote #%d procesado con %d documentos (Acumulado: %d)%n",
                    loteIndex++, lote.size(), totalIterado);
        }

        ConsoleColor.printSuccess("Total de documentos recorridos en streaming: " + totalIterado);
    }
}
