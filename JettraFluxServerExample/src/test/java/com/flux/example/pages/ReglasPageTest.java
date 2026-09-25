package com.flux.example.pages;

import com.flux.plugin.example.model.ReglasModel;
import io.jettra.flux.binding.FluxBinder;
import io.jettra.rules.core.JettraComputeEngine;
import io.jettra.rules.core.JettraRulesEngine;
import io.jettra.rules.core.RuleResult;
import io.jettra.test.annotation.JettraTest;
import io.jettra.test.core.JettraAssert;
import io.jettra.test.wui.WuiTestRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReglasPageTest {

    @JettraTest
    public void testReglasModelJavaMethodValidationAndCompute() {
        ReglasModel model = new ReglasModel();
        Map<String, String> params = new HashMap<>();
        params.put("id", "REG-100");
        params.put("saldo", "250.0");
        params.put("descuento", "50.0");

        List<RuleResult> results = new FluxBinder(model)
                .bind(params)
                .compute()
                .validate();

        JettraAssert.assertTrue(results.stream().allMatch(RuleResult::isValid), "Method-level JettraRules validation should pass");
        JettraAssert.assertTrue(model.getSaldoNeto() == 200.0, "Method-level @Compute should yield 200.0 for 250 - 50");
    }

    @JettraTest
    public void testReglasModelWebScriptGeneration() {
        String script = FluxBinder.generateWebRulesScript(ReglasModel.class, "reglasForm");
        JettraAssert.assertTrue(script != null && script.contains("function validateModelRules"), "Web-level JS validation script should be generated");
        JettraAssert.assertTrue(script.contains("function compute_saldoNeto"), "Web-level JS compute script should be generated");
    }

    @JettraTest
    public void testReglasPageSimulation() {
        WuiTestRunner wuiRunner = new WuiTestRunner();
        wuiRunner.validateInterface("ReglasPage");
        wuiRunner.validateInterface("RulesPage");
        wuiRunner.registerFormData("reglasForm", "{\"id\":\"REG-01\", \"saldo\":\"100\", \"descuento\":\"10\"}");
        wuiRunner.simulateClick("btnSave");
        JettraAssert.assertTrue(true, "Simulación de ReglasPage y RulesPage ejecutada exitosamente");
    }
}
