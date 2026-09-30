package io.jettra.examples.store.model;

/**
 * Registro de Lectura de Sensor con telemetría en tiempo real.
 */
public record SensorReading(
    String deviceId,
    long timestamp,
    double temperature,
    double humidity,
    double voltage
) {}
