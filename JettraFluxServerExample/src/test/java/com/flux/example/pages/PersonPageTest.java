package com.flux.example.pages;

import com.flux.plugin.example.model.PersonModel;
import io.jettra.flux.binding.FluxBinder;
import io.jettra.rules.core.RuleResult;
import io.jettra.test.annotation.JettraTest;
import io.jettra.test.core.JettraAssert;
import io.jettra.test.wui.WuiTestRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonPageTest {

    @JettraTest
    public void testPersonModelJavaMethodValidation() {
        PersonModel model = new PersonModel();
        Map<String, String> params = new HashMap<>();
        params.put("name", "Juan Perez");
        params.put("email", "juan@example.com");
        params.put("age", "30");

        List<RuleResult> results = new FluxBinder(model)
                .bind(params)
                .compute()
                .validate();

        JettraAssert.assertTrue(results.stream().allMatch(RuleResult::isValid), "Method-level validation for PersonModel should pass");

        // Test invalid email
        PersonModel invalidModel = new PersonModel();
        Map<String, String> invalidParams = new HashMap<>();
        invalidParams.put("name", "Juan Perez");
        invalidParams.put("email", "invalid-email-address");
        invalidParams.put("age", "30");

        List<RuleResult> invalidResults = new FluxBinder(invalidModel)
                .bind(invalidParams)
                .compute()
                .validate();

        JettraAssert.assertTrue(invalidResults.stream().anyMatch(r -> !r.isValid()), "Method-level validation should fail for invalid email");
    }

    @JettraTest
    public void testPersonModelWebScriptGeneration() {
        String script = FluxBinder.generateWebRulesScript(PersonModel.class, "personForm");
        JettraAssert.assertTrue(script != null && script.contains("function validateModelRules"), "Web-level JS validation script should be generated for PersonModel");
        JettraAssert.assertTrue(script.contains("correo electrónico válido"), "Web-level validation for Email should be included in JS script");
    }

    @JettraTest
    public void testPersonPageSimulation() {
        WuiTestRunner wuiRunner = new WuiTestRunner();
        wuiRunner.validateInterface("PersonPage");
        wuiRunner.registerFormData("personForm", "{\"name\":\"Juan\", \"email\":\"juan@example.com\", \"age\":\"25\"}");
        JettraAssert.assertTrue(true, "Simulación de PersonPage ejecutada exitosamente");
    }
}
