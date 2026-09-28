package com.avbravo.jcofa.page;

import com.avbravo.jcofa.model.PersonModel;
import com.avbravo.jcofa.template.TemplatePage;
import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Card;
import io.jettra.flux.widgets.TextField;
import io.jettra.flux.widgets.Div;
import io.jettra.flux.widgets.Form;
import io.jettra.flux.widgets.Paragraph;
import io.jettra.flux.widgets.Header;
import io.jettra.flux.widgets.Label;

import com.sun.net.httpserver.HttpExchange;
import io.jettra.core.inject.annotation.InjectProperties;
import io.jettra.flux.core.Widget;
import io.jettra.server.JettraServer;
import io.jettra.core.security.widget.PageWidgetAllow;
import io.jettra.core.security.widget.ActionWidgetAllow;
import io.jettra.flux.annotations.binding.FluxBinding;
import io.jettra.flux.binding.FluxBinder;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.JettraSyncManager;
import io.jettra.flux.sync.SyncType;
import io.jettra.flux.widgets.ElevatedButton;
import io.jettra.flux.widgets.Grid;
import io.jettra.flux.widgets.Row;
import io.jettra.rules.core.RuleResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import io.jettra.core.server.Page;
import io.jettra.flux.widgets.Alert;
import io.jettra.flux.widgets.Icon;
import io.jettra.flux.widgets.Text;
import io.jettra.server.core.JettraContext;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER })
@Page(path = "/person")
public class PersonPage extends TemplatePage {

    @InjectProperties(name = "messages")
    private Properties msg;

    @FluxBinding(model = PersonModel.class)
    PersonModel personModel = new PersonModel();
    List<PersonModel> persons = new ArrayList<>();

    @Override
    public String getTitle() {
        return msg != null ? msg.getProperty("personpage.title", "Persona") : "Persona";
    }

    @ActionWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER })
    private void saveForm(HttpExchange exchange, Map<String, String> params) {
        System.out.println("Formulario recibido con datos: " + params);

        List<RuleResult> results = new FluxBinder(personModel)
                .messages(msg)
                .bind(params)
                .compute()
                .validate();

        boolean hasErrors = false;
        StringBuilder errorMsg = new StringBuilder();
        for (RuleResult result : results) {
            if (!result.isValid()) {
                hasErrors = true;
                if (errorMsg.length() > 0) {
                    errorMsg.append(" | ");
                }
                errorMsg.append(result.getMessage());
            }
        }

        if (hasErrors) {
            try {
                redirect(exchange, "/person?error=" + java.net.URLEncoder.encode(errorMsg.toString(), "UTF-8"));
            } catch (Exception e) {
            }
        } else {
            JettraSyncManager.notifyChange("PersonModel", SyncType.UPDATE, getLoggedUser(exchange));
            io.jettra.flux.widgets.NotificationTop.broadcast("main_notification", io.jettra.flux.widgets.NotificationTop.NotificationTopType.GLOBAL, "Nueva persona agregada: " + personModel.getName());

            try {
                redirect(exchange, "/person?success=true");
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        PersonModel personModel = (PersonModel) JettraContext.getCurrent().get(JettraContext.Scope.SESSION, "personForm");
        if (personModel == null) {
            personModel = new PersonModel();
            JettraContext.getCurrent().set(JettraContext.Scope.SESSION, "personForm", personModel);
        }

        String successMsgStr = (msg != null && msg.getProperty("app.label.success") != null) ? msg.getProperty("app.label.success") : "Registro guardado exitosamente.";
        String defaultErrorMsgStr = (msg != null && msg.getProperty("app.label.error") != null) ? msg.getProperty("app.label.error") : "Error al guardar el registro.";
        String enterYour = (msg != null && msg.getProperty("app.label.enteryour") != null) ? msg.getProperty("app.label.enteryour") : "Ingrese su ";
        String nameLabel = (msg != null && msg.getProperty("person.name") != null) ? msg.getProperty("person.name") : "Nombre";
        String emailLabel = (msg != null && msg.getProperty("person.email") != null) ? msg.getProperty("person.email") : "Correo";
        String ageLabel = (msg != null && msg.getProperty("person.age") != null) ? msg.getProperty("person.age") : "Edad";
        String subtitleStr = (msg != null && msg.getProperty("personpage.subtitle") != null) ? msg.getProperty("personpage.subtitle") : "Registros de Persona";

        Widget alert = null;
        if ("true".equals(params.get("success"))) {
            alert = Alert.of(Text.of(successMsgStr)).severity("success")
                    .modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 20px; width: 100%;"));
        } else if (params.containsKey("error")) {
            String rawErr = params.get("error");
            if (rawErr != null) {
                rawErr = rawErr.replace("+", " ");
            }
            String errorMsgStr = "true".equals(rawErr) ? defaultErrorMsgStr : rawErr;
            alert = Alert.of(
                Column.of(
                    Header.of(4, "Error de Validación de Reglas (JettraRules)")
                            .modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; margin-bottom: 5px; font-weight: 600; font-size: 15px;")),
                    Paragraph.of(errorMsgStr)
                            .modifier(new io.jettra.flux.core.Modifier().style("font-weight: 500; font-size: 14px; margin: 0;"))
                ).modifier(new io.jettra.flux.core.Modifier().style("align-items: flex-start; gap: 4px;"))
            ).severity("danger");
        }

        Widget verticalForm = Card.of(Column.of(
                Header.of(4, subtitleStr)
                        .modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; margin-bottom: 15px; font-weight: 600;")),
                Label.of(nameLabel).forId("name")
                        .modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 5px; font-weight: 500; display: block;")),
                TextField.of(nameLabel, enterYour + nameLabel).id("name")
                        .binding(PersonModel.class, "name")
                        .value(personModel.getName())
                        .modifier(new io.jettra.flux.core.Modifier().style(
                                "margin-bottom: 15px; width: 100%; border: 1px solid #d1d5db; border-radius: 6px; padding: 8px 12px;")),
                Label.of(emailLabel).forId("email")
                        .modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 5px; font-weight: 500; display: block;")),
                TextField.of(emailLabel, enterYour + emailLabel).id("email")
                        .binding(PersonModel.class, "email")
                        .value(personModel.getEmail())
                        .modifier(new io.jettra.flux.core.Modifier().style(
                                "margin-bottom: 15px; width: 100%; border: 1px solid #d1d5db; border-radius: 6px; padding: 8px 12px;")),
                Label.of(ageLabel).forId("age")
                        .modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 5px; font-weight: 500; display: block;")),
                TextField.of(ageLabel, enterYour + ageLabel).id("age")
                        .binding(PersonModel.class, "age")
                        .value(personModel.getAge())
                        .modifier(new io.jettra.flux.core.Modifier().style(
                                "margin-bottom: 15px; width: 100%; border: 1px solid #d1d5db; border-radius: 6px; padding: 8px 12px;")))
                .modifier(new io.jettra.flux.core.Modifier().style("width: 100%; align-items: stretch; gap: 5px;")));

        Widget mainForm = Form.of(
                Column.of(
                        Grid.of(verticalForm).modifier(new io.jettra.flux.core.Modifier().style(
                                "grid-template-columns: 1fr 1fr; gap: 20px; align-items: flex-start; margin-bottom: 20px;")),
                        Row.of(
                                ElevatedButton.of(msg != null ? msg.getProperty("btn.save", "Save") : "Save")
                                        .modifier(new io.jettra.flux.core.Modifier().style(
                                                "align-self: flex-start; padding: 10px 20px; background-color: #6366F1; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 600;"))
                        )
                        .modifier(new io.jettra.flux.core.Modifier()
                                .style("display: flex; flex-direction: row; align-items: center;")))
                .modifier(new io.jettra.flux.core.Modifier().style("width: 100%;")))
                .action(JettraServer.resolvePath("/person?_action_method=saveForm")).method("POST").attribute("id", "personForm");

        Widget webRulesScriptWidget = FluxBinder.generateWebRulesWidget(PersonModel.class, "personForm");

        return Column.of(
                Header.of(2, msg != null ? msg.getProperty("personpage.title", "Persona") : "Persona")
                        .modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; font-weight: 600; margin-bottom: 20px;")),
                (alert != null ? alert : Div.of()),
                mainForm,
                webRulesScriptWidget)
                .modifier(new io.jettra.flux.core.Modifier()
                        .style("width: 100%; align-items: flex-start; max-width: 1200px; padding: 20px;"));
    }
}
