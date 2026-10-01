package io.jettra.examples.store.calc;

import io.jettra.examples.store.driver.JettraDriver;
import io.jettra.examples.store.util.ConsoleColor;
import io.jettra.store.calc.JettraAggregation;
import io.jettra.store.engine.models.DocumentEngine;

import java.util.*;

/**
 * Ejemplo completo que demuestra el uso de las nuevas capacidades avanzadas de JettraStore:
 * 1. Agregaciones multimodelo (GROUP BY, SUM, AVG, COUNT, MIN, MAX, MEDIAN)
 * 2. Operaciones matemáticas cuantitativas y evaluación de expresiones
 * 3. Operaciones financieras (PMT, CAGR, Tabla de Amortización, NPV, IRR)
 * 4. Estadística descriptiva e inferencial (Media, Mediana, Varianza, Regresión, IQR)
 * 5. Álgebra vectorial (Similitud Coseno, Distancia Euclidiana, Producto Cruz 3D, Ángulos)
 */
public class AnalyticsAndCalcExample {

    public static void run(JettraDriver driver) {
        ConsoleColor.printHeader("MÓDULO: ANALÍTICA, AGREGACIONES, MATEMÁTICAS, FINANZAS Y VECTORES");

        String dbName = "analytics_showcase_db";

        // 1. Agregaciones Multimodelo y GROUP BY
        runAggregations(driver, dbName);

        // 2. Operaciones Matemáticas
        runMathematics(driver);

        // 3. Operaciones Financieras
        runFinance(driver);

        // 4. Operaciones Estadísticas
        runStatistics(driver);

        // 5. Álgebra Vectorial
        runVectors(driver);
    }

    private static void runAggregations(JettraDriver driver, String dbName) {
        ConsoleColor.printSubHeader("1. Agregaciones Multimodelo y GROUP BY (JettraAggregation)");
        DocumentEngine sales = driver.getDocumentEngine(dbName, "sales_orders");

        sales.insert("ord_1", Map.of("region", "Norte", "categoria", "Hardware", "monto", 1250.0, "unidades", 5));
        sales.insert("ord_2", Map.of("region", "Norte", "categoria", "Software", "monto", 450.0, "unidades", 1));
        sales.insert("ord_3", Map.of("region", "Norte", "categoria", "Hardware", "monto", 3200.0, "unidades", 12));
        sales.insert("ord_4", Map.of("region", "Sur",   "categoria", "Hardware", "monto", 850.0, "unidades", 3));
        sales.insert("ord_5", Map.of("region", "Sur",   "categoria", "Software", "monto", 2100.0, "unidades", 4));
        sales.insert("ord_6", Map.of("region", "Sur",   "categoria", "Software", "monto", 1500.0, "unidades", 2));

        // Ejecutar agregación con JettraDriver API
        var aggResult = driver.aggregateSum(dbName, "sales_orders", "monto", "region");
        ConsoleColor.printSuccess("Agregación SUM(monto) por region computada. Total grupos: " + aggResult.totalGroups());
        for (var row : aggResult.rows()) {
            ConsoleColor.printInfo("Región " + row.get("region"), "Total Ventas: $" + row.get("total_monto"));
        }

        // Ejecutar agregación múltiple vía SQL GROUP BY
        var sqlRes = driver.sql(dbName,
            "SELECT region, SUM(monto) AS total_ventas, AVG(monto) AS promedio, MEDIAN(monto) AS mediana, COUNT(*) AS total_ordenes " +
            "FROM sales_orders GROUP BY region;");
        ConsoleColor.printSuccess("SQL Agregación ejecutada con éxito. Columnas: " + sqlRes.columns());
        for (var row : sqlRes.rows()) {
            ConsoleColor.printInfo("Fila SQL", row);
        }
    }

    private static void runMathematics(JettraDriver driver) {
        ConsoleColor.printSubHeader("2. Operaciones Matemáticas y Evaluador Cuantitativo (JettraMath)");

        double expr = driver.evalMath("cbrt(64) + sqrt(144) * 2 - hypot(3, 4) + fact(5)");
        ConsoleColor.printInfo("Evaluación Matemática", expr);

        long gcd = driver.gcd(1071, 462);
        long lcm = driver.lcm(24, 60);
        ConsoleColor.printSuccess(String.format("GCD(1071, 462) = %d | LCM(24, 60) = %d | 7! = %d", gcd, lcm, driver.factorial(7)));
    }

    private static void runFinance(JettraDriver driver) {
        ConsoleColor.printSubHeader("3. Operaciones Financieras Cuantitativas (JettraFinance)");

        // Cálculo de cuota periódica fija (PMT)
        double principal = 250000.0;
        double annualRate = 0.055; // 5.5%
        int months = 360; // 30 años
        double monthlyPmt = driver.pmt(annualRate / 12.0, months, principal);
        ConsoleColor.printInfo("Cuota Mensual PMT", String.format("$%,.2f (Tasa %.2f%%, %d meses)", monthlyPmt, annualRate * 100.0, months));

        // Tasa de Crecimiento Anual Compuesto (CAGR)
        double cagr = driver.cagr(150000.0, 480000.0, 5.0);
        ConsoleColor.printSuccess(String.format("CAGR (De 50k a 80k en 5 años): %.2f%% anual", cagr));

        // Tabla de amortización francesa (primeros 3 períodos)
        var sched = driver.amortizationSchedule(10000.0, 0.06, 12);
        ConsoleColor.printSuccess("Cronograma de Amortización Francesa (muestra de primeros 3 períodos):");
        for (int i = 0; i < 3; i++) {
            var row = sched.get(i);
            ConsoleColor.printInfo("Período " + row.period(), String.format("Cuota: $%.2f | Saldo: $%.2f", row.payment(), row.remainingBalance()));
        }
    }

    private static void runStatistics(JettraDriver driver) {
        ConsoleColor.printSubHeader("4. Estadística Descriptiva e Inferencial (JettraStatistics)");

        List<Double> telemetry = List.of(15.2, 18.5, 21.0, 22.4, 25.8, 30.1, 35.6, 42.0, 48.9, 60.5);
        var summary = driver.statsSummary(telemetry);
        double iqr = driver.statsIqr(telemetry);

        ConsoleColor.printInfo("Resumen Estadístico", String.format("Media: %.2f | Mediana: %.2f | StdDev: %.2f | IQR: %.2f", summary.mean(), summary.median(), summary.stddev(), iqr));

        // Correlación y Regresión lineal
        List<Double> x = List.of(1.0, 2.0, 3.0, 4.0, 5.0);
        List<Double> y = List.of(2.2, 3.9, 6.1, 8.2, 10.1);
        double r = driver.statsCorrelation(x, y);
        var reg = driver.statsLinearRegression(x, y);
        ConsoleColor.printSuccess(String.format("Correlación r = %.4f | Regresión: y = %.2fx + %.2f (R² = %.4f)",
            r, reg.slope(), reg.intercept(), reg.rSquared()));
    }

    private static void runVectors(JettraDriver driver) {
        ConsoleColor.printSubHeader("5. Álgebra Vectorial y Machine Learning (JettraVectorMath)");

        float[] embPrompt = new float[]{0.85f, 0.15f, 0.50f};
        float[] embDoc1   = new float[]{0.82f, 0.18f, 0.48f};
        float[] embDoc2   = new float[]{0.10f, 0.90f, 0.20f};

        float sim1 = driver.cosineSimilarity(embPrompt, embDoc1);
        float sim2 = driver.cosineSimilarity(embPrompt, embDoc2);
        float dist1 = driver.euclideanDistance(embPrompt, embDoc1);

        ConsoleColor.printInfo("Similitud (Prompt vs Doc1)", String.format("Coseno: %.4f | Euclidiana: %.4f", sim1, dist1));
        ConsoleColor.printInfo("Similitud (Prompt vs Doc2)", String.format("Coseno: %.4f (Lejana)", sim2));

        // Operaciones de Geometría Vectorial 3D
        float[] v1 = new float[]{1f, 0f, 0f};
        float[] v2 = new float[]{0f, 1f, 0f};
        float[] cross = driver.crossProduct(v1, v2);
        double angle = driver.vectorAngleDegrees(v1, v2);

        ConsoleColor.printSuccess(String.format("Vector [1,0,0] x [0,1,0] = %s | Ángulo: %.1f°",
            Arrays.toString(cross), angle));
    }
}
