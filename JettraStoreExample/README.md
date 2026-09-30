# JettraStoreExample 🚀 (Java 25 + Maven)

Proyecto de referencia y demostración completa de **JettraStore** implementando **JettraDriver**. Expone ejemplos funcionales con cada uno de los motores multimodelo integrados, operando tanto en memoria **RAM** (`StorageMode.JVM_RAM`) como en disco directo **DISK_MEMORY** (`StorageMode.DISK_MEMORY`) mediante Panama FFM API con **JettraMemory**.

---

## 📋 Características Principales

- **Java 25 LTS:** Aprovecha *Virtual Threads* (`Thread.ofVirtual()`), *Records*, *Pattern Matching* y *Project Panama FFM API* (Foreign Function & Memory API).
- **JettraDriver:** Interfaz y conector unificado (`io.jettra.examples.store.driver.JettraDriver`) con soporte de gestión de catálogo, transiciones de almacenamiento y acceso directo a motores.
- **Multimodelo Completo (9 Motores):**
  1. 📄 **DocumentEngine:** NoSQL tipo JSON/BSON con control concurrente RWLock y listas perezosas.
  2. 🔑 **KeyValueEngine:** Caché de ultra-baja latencia O(1) con soporte directo de buffers binarios.
  3. 🧠 **VectorEngine:** Indexación de embeddings IA multidimensionales y búsqueda semántica por similitud coseno (*Cosine Similarity*).
  4. 🕸️ **GraphEngine:** Grafos con vértices, aristas dirigidas tipadas y propiedades arbitrarias.
  5. ⏱️ **TimeSeriesEngine:** Telemetría de alta frecuencia, consultas por ventanas de tiempo y promedios en ventana.
  6. 🗺️ **GeospatialEngine:** Coordenadas terrestres (lat/lon), cálculo geodésico Haversine y búsqueda por radio en Km.
  7. 📊 **ColumnarEngine:** Tablas analíticas en memoria para OLAP con agregaciones vectorizadas (`sumColumn`).
  8. ☕ **RecordsEngine:** Persistencia fuertemente tipada sobre Java 25 Records inmutables con cero mapeos ORM.
  9. ⚡ **JettraMemory (Off-Heap Panama):** Almacenamiento binario nativo fuera del Garbage Collector (Zero-GC pressure).
- **Modos de Almacenamiento:**
  - `StorageMode.JVM_RAM`: Manipulación en memoria de la JVM para velocidad máxima.
  - `StorageMode.DISK_MEMORY`: Persistencia directa en disco Off-Heap LSM con compactación en caliente.
- **Consultas JettraSQL y Streaming:** Consultas `sql()`, paginación acotada `sqlPaged()` y cursores distribuidos `LazyPagedCursor` O(1) de Heap.

---

## 📂 Estructura del Proyecto

```
JettraStoreExample/
├── pom.xml
├── README.md
└── src/
    ├── main/java/io/jettra/examples/store/
    │   ├── JettraStoreExampleApp.java        # Ejecutor principal de todos los motores
    │   ├── driver/
    │   │   ├── JettraDriver.java             # Contrato de la API del Driver
    │   │   └── JettraDriverImpl.java         # Implementación sobre JettraClient
    │   ├── engines/
    │   │   ├── DocumentEngineExample.java    # Ejemplo Motor de Documentos
    │   │   ├── KeyValueEngineExample.java    # Ejemplo Motor Clave-Valor
    │   │   ├── VectorEngineExample.java      # Ejemplo Motor Vectorial (Embeddings)
    │   │   ├── GraphEngineExample.java       # Ejemplo Motor de Grafos
    │   │   ├── TimeSeriesEngineExample.java  # Ejemplo Motor Series Temporales
    │   │   ├── GeospatialEngineExample.java  # Ejemplo Motor Geoespacial
    │   │   ├── ColumnarEngineExample.java    # Ejemplo Motor Columnar (OLAP)
    │   │   ├── RecordsEngineExample.java     # Ejemplo Pure Object / Records
    │   │   └── MemoryOffHeapExample.java     # Ejemplo Off-Heap Panama FFM
    │   ├── storage/
    │   │   └── StorageModeComparisonExample.java # Benchmark y comparativa RAM vs DISK_MEMORY
    │   ├── query/
    │   │   └── SqlAndCursorExample.java      # Consultas SQL, sqlPaged y Lazy Cursor
    │   ├── model/
    │   │   ├── ProductRecord.java            # Record de producto para RecordsEngine
    │   │   └── SensorReading.java            # Record de telemetría IoT
    │   └── util/
    │       └── ConsoleColor.java             # Formato visual con colores ANSI
    └── test/java/io/jettra/examples/store/
        └── JettraDriverEnginesTest.java      # Pruebas unitarias JUnit 5 de todos los motores
```

---

## 🚀 Compilación y Ejecución

### Prerrequisitos
- **Java 25+** (con `--enable-preview`)
- **Maven 3.9+**

### 1. Compilar el proyecto
```bash
mvn clean compile
```

### 2. Ejecutar las pruebas unitarias
```bash
mvn test
```

### 3. Ejecutar la demostración completa interactiva
```bash
mvn exec:java
```

---

## 💻 Código de Ejemplo Rápido

```java
import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.store.core.StorageMode;

// 1. Abrir conexión con el driver
try (JettraDriver driver = JettraDriver.open()) {

    // 2. Obtener base de datos en modo RAM o DISK_MEMORY
    String dbName = "mi_tienda_db";
    driver.getDatabase(dbName, StorageMode.JVM_RAM);

    // 3. Trabajar con DocumentEngine
    var docs = driver.getDocumentEngine(dbName, "productos");
    docs.insert("p1", Map.of("nombre", "Laptop Pro", "precio", 1999.00));

    // 4. Trabajar con VectorEngine (Embeddings IA)
    var vectors = driver.getVectorEngine(dbName, "embeddings", 3);
    vectors.index("v1", new float[]{0.95f, 0.10f, 0.20f});
    var matches = vectors.searchCosine(new float[]{0.90f, 0.12f, 0.22f}, 1);

    // 5. Conmutar a DISK_MEMORY
    driver.setStorageMode(dbName, StorageMode.DISK_MEMORY);
}
```
