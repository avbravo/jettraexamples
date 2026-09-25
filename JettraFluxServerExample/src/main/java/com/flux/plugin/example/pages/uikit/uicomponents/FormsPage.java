package com.flux.plugin.example.pages.uikit.uicomponents;

import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Card;
import io.jettra.flux.widgets.TextField;
import io.jettra.flux.widgets.Div;
import io.jettra.flux.widgets.Form;
import io.jettra.flux.widgets.Paragraph;
import io.jettra.flux.widgets.Header;
import io.jettra.flux.widgets.Label;
import io.jettra.flux.widgets.Notification;
import com.flux.example.pages.template.TemplatePage;
import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import io.jettra.server.JettraServer;
import io.jettra.core.security.widget.PageWidgetAllow;
import io.jettra.core.security.widget.ActionWidgetAllow;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;
import java.util.Map;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role={jcf.AppRole.ADMIN, jcf.AppRole.MANAGER}) 
@io.jettra.core.server.Page(path = "/forms")
public class FormsPage extends TemplatePage {

    @Override
    public String getTitle() {
        return "Forms - JettraFlux Pro";
    }

    @ActionWidgetAllow(role={jcf.AppRole.ADMIN, jcf.AppRole.MANAGER})
    private void saveForm(HttpExchange exchange, Map<String, String> params) {
       IO.println("Formulario recibido con datos (Método de acción seguro): " + params);
        io.jettra.flux.sync.JettraSyncManager.notifyChange("FormsModel", io.jettra.flux.sync.SyncType.UPDATE, getLoggedUser(exchange));
        try { redirect(exchange, "/forms?success=true"); } catch (Exception e) {}
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        // Fallback para ActionBinder en caso de no usar _action_method (Se deja por compatibilidad si es necesario, 
        // pero preferimos el _action_method)
        new io.jettra.flux.core.ActionBinder(params)
            .on("nombre", p -> {
                // Ignore, handled by saveForm if _action_method is present
            })
            .execute();

        Widget alert = null;
        if ("true".equals(params.get("success"))) {
            alert = Notification.of(Paragraph.of("¡Formulario enviado correctamente!"));
        }

        // --- Vertical Form ---
        Widget verticalForm = Card.of(Column.of(
            Header.of(4, "Vertical").modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; margin-bottom: 15px; font-weight: 600;")),
            
            Label.of("Name").forId("name").modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 5px; font-weight: 500; display: block;")),
            TextField.of("name", "Enter your name").modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 15px; width: 100%; border: 1px solid #d1d5db; border-radius: 6px; padding: 8px 12px;")),
            
            Label.of("Email").forId("email").modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 5px; font-weight: 500; display: block;")),
            TextField.of("email", "Enter your email").modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 15px; width: 100%; border: 1px solid #d1d5db; border-radius: 6px; padding: 8px 12px;")),
            
            Label.of("Age").forId("age").modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 5px; font-weight: 500; display: block;")),
            TextField.of("age", "Enter your age").modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 15px; width: 100%; border: 1px solid #d1d5db; border-radius: 6px; padding: 8px 12px;"))
        ).modifier(new io.jettra.flux.core.Modifier().style("width: 100%; align-items: stretch; gap: 5px;")));

        // --- Horizontal Form ---
        Widget horizontalForm = Card.of(Column.of(
            Header.of(4, "Horizontal").modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; margin-bottom: 15px; font-weight: 600;")),
            
            io.jettra.flux.widgets.Row.of(
                Label.of("Name").forId("h_name").modifier(new io.jettra.flux.core.Modifier().style("width: 100px; font-weight: 500;")),
                TextField.of("h_name", "Enter your name").modifier(new io.jettra.flux.core.Modifier().style("flex: 1; border: 1px solid #d1d5db; border-radius: 6px; padding: 8px 12px;"))
            ).modifier(new io.jettra.flux.core.Modifier().style("align-items: center; margin-bottom: 15px;")),
            
            io.jettra.flux.widgets.Row.of(
                Label.of("Email").forId("h_email").modifier(new io.jettra.flux.core.Modifier().style("width: 100px; font-weight: 500;")),
                TextField.of("h_email", "Enter your email").modifier(new io.jettra.flux.core.Modifier().style("flex: 1; border: 1px solid #d1d5db; border-radius: 6px; padding: 8px 12px;"))
            ).modifier(new io.jettra.flux.core.Modifier().style("align-items: center; margin-bottom: 15px;"))
        ).modifier(new io.jettra.flux.core.Modifier().style("width: 100%; align-items: stretch; gap: 5px;")));

        // Submit form wrapper
        Widget mainForm = Form.of(
            Column.of(
                io.jettra.flux.widgets.Grid.of(verticalForm, horizontalForm).modifier(new io.jettra.flux.core.Modifier().style("grid-template-columns: 1fr 1fr; gap: 20px; align-items: flex-start; margin-bottom: 20px;")),
                io.jettra.flux.widgets.ElevatedButton.of("Guardar Cambios").modifier(new io.jettra.flux.core.Modifier().style("align-self: flex-start; padding: 10px 20px; background-color: #6366F1; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 600;"))
            ).modifier(new io.jettra.flux.core.Modifier().style("width: 100%;"))
        ).action(JettraServer.resolvePath("/forms?_action_method=saveForm")).method("POST");

        // --- Center Content ---
        return Column.of(
            Header.of(2, "Form Layout").modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; font-weight: 600; margin-bottom: 20px;")),
            (alert != null ? alert : Div.of()),
            mainForm
        ).modifier(new io.jettra.flux.core.Modifier().style("width: 100%; align-items: flex-start; max-width: 1200px; padding: 20px;"));
    }
}
