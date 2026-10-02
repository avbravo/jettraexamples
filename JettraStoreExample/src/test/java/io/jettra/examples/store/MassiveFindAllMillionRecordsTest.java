package io.jettra.examples.store;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.query.MassiveFindAllMillionRecordsExample;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MassiveFindAllMillionRecordsTest {

    @Test
    @DisplayName("Debe ejecutar la evaluación de consultas masivas findAll() sobre example_factura_db")
    void testMassiveFindAllExecution() {
        try (JettraDriver driver = JettraDriver.open()) {
            assertDoesNotThrow(() -> {
                MassiveFindAllMillionRecordsExample.run(driver);
            });
        }
    }
}
