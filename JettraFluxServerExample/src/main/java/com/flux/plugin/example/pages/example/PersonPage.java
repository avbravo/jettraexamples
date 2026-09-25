package com.flux.plugin.example.pages.example;

import com.flux.plugin.example.model.PersonModel;
import com.flux.example.pages.template.TemplatePage;
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

//   @Inject
//    PersonModelConverter personModelConverter;
    
    /**
     * Model
     */
    @FluxBinding(model = PersonModel.class)
    PersonModel personModel = new PersonModel();
    List<PersonModel> persons = new ArrayList<>();

    @Override
    public String getTitle() {
        return msg.getProperty("personpage.title");
    }

    @ActionWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER })
    private void saveForm(HttpExchange exchange, Map<String, String> params) {
    
        IO.println("Formulario recibido con datos (Método de acción seguro): " + params);

       
        
        List<RuleResult> results = new FluxBinder(personModel)
                .messages(msg)
                .bind(params)
                .compute()
                .validate();

        IO.print("--> personModel " + personModel.toString() + " " + personModel.getName());
        
//        IO.println("Conversor to Record"+ personModelConverter.toRecord(personModel).toString());
         
       IO.println("results --> "+results);
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
            IO.println("Error de validación: " + errorMsg.toString());
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
       // Properties msg = (Properties) JettraContext.getCurrent().get(JettraContext.Scope.REQUEST, "messages");
        
       // Muestra como guardar y recuperar objetos de la sesion
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

        // --- Vertical Form ---
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

        // Submit form wrapper
        Widget mainForm = Form.of(
                Column.of(
                        Grid.of(verticalForm).modifier(new io.jettra.flux.core.Modifier().style(
                                "grid-template-columns: 1fr 1fr; gap: 20px; align-items: flex-start; margin-bottom: 20px;")),
                        Row.of(
                                ElevatedButton.of(msg != null ? msg.getProperty("btn.save") : "Save")
                                        .modifier(new io.jettra.flux.core.Modifier().style(
                                                "align-self: flex-start; padding: 10px 20px; background-color: #6366F1; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 600;")),
                                
                                ElevatedButton.of(Row.of(Icon.of("fas fa-globe"), Text.of(" Send Global").modifier(new io.jettra.flux.core.Modifier().style("margin-left:5px;"))))
                                        .modifier(new io.jettra.flux.core.Modifier().style("margin-left:10px; background-color: #0d6efd; color: white; padding: 10px 15px; border:none; border-radius:6px; cursor:pointer; font-weight:600;"))
                                        .onClick(c -> {
                                            if (hasRoleAdminOrManager()) {
                                                io.jettra.flux.widgets.NotificationTop.broadcast("global_notif", io.jettra.flux.widgets.NotificationTop.NotificationTopType.GLOBAL, "Alerta Global: Sistema actualizado");
                                            }
                                        }),
                                        
                                ElevatedButton.of(Row.of(Icon.of("fas fa-envelope"), Text.of(" Send Personal").modifier(new io.jettra.flux.core.Modifier().style("margin-left:5px;"))))
                                        .modifier(new io.jettra.flux.core.Modifier().style("margin-left:10px; background-color: #17a2b8; color: white; padding: 10px 15px; border:none; border-radius:6px; cursor:pointer; font-weight:600;"))
                                        .onClick(c -> {
                                            if (hasRoleAdminOrManager()) {
                                                io.jettra.flux.widgets.NotificationTop.broadcast("personal_notif", io.jettra.flux.widgets.NotificationTop.NotificationTopType.PERSONAL, "Mensaje Privado: Tienes un nuevo correo");
                                            }
                                        }),
                                        
                                ElevatedButton.of(Row.of(Icon.of("fas fa-bullhorn"), Text.of(" Send Channel").modifier(new io.jettra.flux.core.Modifier().style("margin-left:5px;"))))
                                        .modifier(new io.jettra.flux.core.Modifier().style("margin-left:10px; background-color: #dc3545; color: white; padding: 10px 15px; border:none; border-radius:6px; cursor:pointer; font-weight:600;"))
                                        .onClick(c -> {
                                            if (hasRoleAdminOrManager()) {
                                                io.jettra.flux.widgets.NotificationTop.broadcast("channel_notif", io.jettra.flux.widgets.NotificationTop.NotificationTopType.CHANNEL, "Canal Admins: Nuevo reporte disponible");
                                            }
                                        })
                                )
                                .modifier(new io.jettra.flux.core.Modifier()
                                        .style("display: flex; flex-direction: row; align-items: center;")))
                        .modifier(new io.jettra.flux.core.Modifier().style("width: 100%;")))
                .action(JettraServer.resolvePath("/person?_action_method=saveForm")).method("POST").attribute("id", "personForm");

        // Automatically attach browser-level JettraRules Web Validation JavaScript Widget
        Widget webRulesScriptWidget = FluxBinder.generateWebRulesWidget(PersonModel.class, "personForm");

        // --- Center Content ---
        return Column.of(
                Header.of(2, msg.getProperty("personpage.title"))
                        .modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; font-weight: 600; margin-bottom: 20px;")),
                (alert != null ? alert : Div.of()),
                mainForm,
                webRulesScriptWidget)
                .modifier(new io.jettra.flux.core.Modifier()
                        .style("width: 100%; align-items: flex-start; max-width: 1200px; padding: 20px;"));
    }

    private boolean hasRoleAdminOrManager() {
        String role = io.jettra.server.core.JettraContext.Scope.SESSION.getRole();
        return "ADMIN".equals(role) || "MANAGER".equals(role);
    }
}
