package io.jettra.examples.store.engines;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.model.ProductRecord;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.core.StorageMode;
import io.jettra.store.engine.models.RecordsEngine;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ejemplo completo del Motor Pure Object / Records (RecordsEngine).
 * Aprovecha los Records inmutables fuertemente tipados de Java 25 para almacenar y
 * recuperar entidades de dominio con cero mapeos ORM pesados y máxima seguridad de tipos.
 */
public class RecordsEngineExample {

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("8. MOTOR PURE OBJECT / RECORDS (RecordsEngine)");

        // 1. Entidad en memoria RAM
        String ramDb = "inventory_ram_db";
        driver.getDatabase(ramDb, StorageMode.JVM_RAM);
        ConsoleColor.printSubHeader("Ejecución en modo RAM (StorageMode.JVM_RAM)");
        testRecordsEngine(driver, ramDb, "catalogo_productos");

        // 2. Entidad en DISK_MEMORY
        String diskDb = "inventory_disk_db";
        driver.getDatabase(diskDb, StorageMode.DISK_MEMORY);
        ConsoleColor.printSubHeader("Ejecución en modo DISK_MEMORY (StorageMode.DISK_MEMORY)");
        testRecordsEngine(driver, diskDb, "catalogo_historico");
    }

    private static void testRecordsEngine(JettraDriver driver, String dbName, String entityName) {
        RecordsEngine<ProductRecord> engine = driver.getRecordsEngine(dbName, entityName, ProductRecord.class);

        ConsoleColor.printInfo("Base de datos", dbName + " [Modo: " + driver.getStorageMode(dbName) + "]");
        ConsoleColor.printInfo("Entidad Record", entityName + " (" + engine.getRecordClass().getSimpleName() + ")");

        // Persistencia directa de instancias Java Record
        ProductRecord p1 = new ProductRecord(
                "SKU-1001",
                "Laptop DevStation 25",
                "Equipos",
                new BigDecimal("2899.00"),
                25,
                true
        );

        ProductRecord p2 = new ProductRecord(
                "SKU-1002",
                "Monitor OLED Ultrawide 49\"",
                "Periféricos",
                new BigDecimal("1299.50"),
                14,
                true
        );

        ProductRecord p3 = new ProductRecord(
                "SKU-1003",
                "Teclado Mecánico Silencioso",
                "Accesorios",
                new BigDecimal("149.99"),
                50,
                true
        );

        engine.persist(p1.sku(), p1);
        engine.persist(p2.sku(), p2);
        engine.persist(p3.sku(), p3);

        ConsoleColor.printSuccess("3 Records persistidos directamente.");
        ConsoleColor.printInfo("Total de registros almacenados", engine.size());

        // Búsqueda tipada por Clave
        ProductRecord found = engine.find("SKU-1001");
        if (found != null) {
            ConsoleColor.printSuccess("Record recuperado por SKU:");
            ConsoleColor.printInfo("  SKU", found.sku());
            ConsoleColor.printInfo("  Nombre", found.name());
            ConsoleColor.printInfo("  Precio", "$" + found.price());
            ConsoleColor.printInfo("  Stock", found.stock());
        }

        // Listado completo de Records
        List<ProductRecord> all = engine.listAll();
        ConsoleColor.printInfo("Recorrido de la lista tipada:", "");
        for (ProductRecord rec : all) {
            System.out.printf("    -> %s: %-30s | Categoría: %-12s | Precio: $%s%n",
                    rec.sku(), rec.name(), rec.category(), rec.price());
        }
    }
}
