package io.jettra.examples.store.model;

import java.math.BigDecimal;

/**
 * Registro de Producto usando Java 25 Records para RecordsEngine.
 */
public record ProductRecord(
    String sku,
    String name,
    String category,
    BigDecimal price,
    int stock,
    boolean active
) {}
