package com.flux.plugin.example.pages.example;

import com.flux.plugin.example.model.ReglasModel;
import com.flux.example.pages.template.TemplatePage;
import com.sun.net.httpserver.HttpExchange;
import io.jettra.core.inject.annotation.InjectProperties;
import io.jettra.core.security.widget.ActionWidgetAllow;
import io.jettra.core.security.widget.PageWidgetAllow;
import io.jettra.core.server.Page;
import io.jettra.flux.annotations.binding.FluxBinding;
import io.jettra.flux.binding.FluxBinder;
import io.jettra.flux.core.Modifier;
import io.jettra.flux.core.Widget;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;
import io.jettra.flux.widgets.*;
import io.jettra.rules.core.RuleResult;
import io.jettra.server.JettraServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER }, department = "")
@Page(path = "/reglas")
public class ReglasPage extends TemplatePage {

    @InjectProperties(name = "messages")
    private Properties msg;

    @FluxBinding(model = ReglasModel.class)
    ReglasModel reglasModel = new ReglasModel("REG-01", 100.0, 15.0, 85.0);

    List<ReglasModel> list = new ArrayList<>();

    @Override
    public String getTitle() {
        return msg != null ? msg.getProperty("title.reglas", "JettraRules Management") : "JettraRules Management";
    }

    @ActionWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER })
    private void saveForm(HttpExchange exchange, Map<String, String> params) {
        // Backend Java method-level binding, compute & validation
        List<RuleResult> results = new FluxBinder(reglasModel)
                .messages(msg)
                .bind(params)
                .compute()
                .validate();

        boolean hasErrors = false;
        StringBuilder errorMsg = new StringBuilder();
        for (RuleResult res : results) {
            if (!res.isValid()) {
                hasErrors = true;
                if (errorMsg.length() > 0) errorMsg.append(" | ");
                errorMsg.append(res.getMessage());
            }
        }

        if (hasErrors) {
            try {
                redirect(exchange, "/reglas?error=" + java.net.URLEncoder.encode(errorMsg.toString(), "UTF-8"));
            } catch (Exception e) {}
        } else {
            list.add(new ReglasModel(reglasModel.getId(), reglasModel.getSaldo(), reglasModel.getDescuento(), reglasModel.getSaldoNeto()));
            try {
                redirect(exchange, "/reglas?success=true");
            } catch (Exception e) {}
        }
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        Widget form = Form.of(
            Card.of(
                Column.of(
                    Header.of(3, "Formulario JettraRules (@Rules y @Compute)"),
                    Paragraph.of("Validación en navegador (Web JS) y servidor (Java método):"),
                    Label.of("ID").forId("id"),
                    TextField.of("id", "ID").value(reglasModel.getId()).attribute("id", "id").binding(ReglasModel.class, "id"),
                    Label.of("Saldo").forId("saldo"),
                    TextField.of("saldo", "Saldo").value(reglasModel.getSaldo().toString()).attribute("id", "saldo").binding(ReglasModel.class, "saldo"),
                    Label.of("Descuento").forId("descuento"),
                    TextField.of("descuento", "Descuento").value(reglasModel.getDescuento().toString()).attribute("id", "descuento").binding(ReglasModel.class, "descuento"),
                    Label.of("Saldo Neto (@Compute)").forId("saldoNeto"),
                    TextField.of("saldoNeto", "Saldo Neto").value(reglasModel.getSaldoNeto().toString()).attribute("id", "saldoNeto").binding(ReglasModel.class, "saldoNeto").attribute("readonly", "readonly")
                             .modifier(new Modifier().style("background-color: rgba(255,255,255,0.1); cursor: not-allowed; font-weight: bold;")),
                    ElevatedButton.of("Guardar").attribute("id", "btnSave").modifier(new Modifier().style("margin-top: 15px; width: 100%; background-color: var(--primary-color, #3b82f6); color: white; border: none;"))
                ).modifier(new Modifier().style("gap: 10px;"))
            ).modifier(new Modifier().style("max-width: 500px; margin: 0 auto; padding: 20px;"))
        ).action(JettraServer.resolvePath("/reglas")).method("POST").attribute("id", "reglasForm");

        // Automatically attach browser-level JettraRules Web Validation & Compute JavaScript Widget
        Widget webRulesScriptWidget = FluxBinder.generateWebRulesWidget(ReglasModel.class, "reglasForm");

        Widget alert = null;
        if (params.containsKey("error")) {
            String errStr = params.get("error");
            if (errStr != null) errStr = errStr.replace("+", " ");
            alert = Alert.of(Text.of("Error: " + errStr)).severity("danger");
        } else if (params.containsKey("success")) {
            alert = Alert.of(Text.of("Registro guardado exitosamente")).severity("success");
        }

        if (alert != null) {
            return Column.of(alert, form, webRulesScriptWidget);
        }
        return Column.of(form, webRulesScriptWidget);
    }
}
