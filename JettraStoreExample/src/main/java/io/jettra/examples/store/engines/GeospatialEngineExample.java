package io.jettra.examples.store.engines;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.core.StorageMode;
import io.jettra.store.engine.models.GeospatialEngine;

import java.util.List;

/**
 * Ejemplo completo del Motor Geoespacial (GeospatialEngine).
 * Permite indexar coordenadas geográficas (latitud, longitud) y ejecutar
 * búsquedas de proximidad espacial en base a la fórmula de Haversine dentro de un radio en Km.
 */
public class GeospatialEngineExample {

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("6. MOTOR GEOESPACIAL (GeospatialEngine)");

        // 1. Capa geoespacial en memoria RAM
        String ramDb = "logistics_ram_db";
        driver.getDatabase(ramDb, StorageMode.JVM_RAM);
        ConsoleColor.printSubHeader("Ejecución en modo RAM (StorageMode.JVM_RAM)");
        testGeospatial(driver, ramDb, "sucursales");

        // 2. Capa geoespacial en DISK_MEMORY
        String diskDb = "logistics_disk_db";
        driver.getDatabase(diskDb, StorageMode.DISK_MEMORY);
        ConsoleColor.printSubHeader("Ejecución en modo DISK_MEMORY (StorageMode.DISK_MEMORY)");
        testGeospatial(driver, diskDb, "vehiculos_reparto");
    }

    private static void testGeospatial(JettraDriver driver, String dbName, String layerName) {
        GeospatialEngine geo = driver.getGeospatialEngine(dbName, layerName);

        ConsoleColor.printInfo("Base de datos", dbName + " [Modo: " + driver.getStorageMode(dbName) + "]");
        ConsoleColor.printInfo("Capa Espacial (Layer)", layerName);

        // Registro de puntos geográficos (Ciudad de Panamá y alrededores)
        geo.insertPoint("punto_centro_financiero", 8.9824, -79.5199);
        geo.insertPoint("punto_casco_antiguo", 8.9513, -79.5342);
        geo.insertPoint("punto_canal_miraflores", 8.9972, -79.5888);
        geo.insertPoint("punto_aeropuerto_tocumen", 9.0714, -79.3835);
        geo.insertPoint("punto_colon_puerto", 9.3598, -79.9000);

        ConsoleColor.printSuccess("5 puntos de interés geográfico indexados.");
        ConsoleColor.printInfo("Total de puntos en capa", geo.size());

        // Búsqueda espacial de puntos dentro de un radio de 10 Km desde el Centro Financiero
        double centroLat = 8.9824;
        double centroLon = -79.5199;
        double radioKm = 10.0;

        ConsoleColor.printInfo("Búsqueda en Radio Espacial",
                radioKm + " Km desde Centro (" + centroLat + ", " + centroLon + ")");

        List<GeospatialEngine.GeoDistanceResult> dentroRadio = geo.findWithinRadius(centroLat, centroLon, radioKm);

        for (GeospatialEngine.GeoDistanceResult r : dentroRadio) {
            System.out.printf("    -> ID: %-28s | Distancia: %.2f Km%n",
                    r.id(), r.distanceKm());
        }

        ConsoleColor.printSuccess("Puntos encontrados dentro del radio de " + radioKm + " Km: " + dentroRadio.size());
    }
}
