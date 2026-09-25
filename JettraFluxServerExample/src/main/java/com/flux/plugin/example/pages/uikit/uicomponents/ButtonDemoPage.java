package com.flux.plugin.example.pages.uikit.uicomponents;

import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import java.util.Map;
import com.flux.example.pages.template.TemplatePage;

import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Paragraph;
import io.jettra.flux.widgets.Card;
import io.jettra.flux.widgets.Header;
import io.jettra.flux.widgets.Row;
import io.jettra.flux.widgets.JettraButton;
import io.jettra.core.security.widget.ActionWidgetAllow;
import io.jettra.core.security.widget.PageWidgetAllow;
import io.jettra.core.server.Page;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;
import io.jettra.server.JettraServer;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER, jcf.AppRole.USER })
@Page(path = "/button-demo")
public class ButtonDemoPage extends TemplatePage {

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget solidButtons = Card.of(Column.of(
            Header.of(4, "Solid Buttons").modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 1rem;")),
            Row.of(
                JettraButton.of("Primary").severity(JettraButton.Severity.PRIMARY),
                JettraButton.of("Secondary").severity(JettraButton.Severity.SECONDARY),
                JettraButton.of("Success").severity(JettraButton.Severity.SUCCESS),
                JettraButton.of("Info").severity(JettraButton.Severity.INFO),
                JettraButton.of("Warning").severity(JettraButton.Severity.WARNING),
                JettraButton.of("Help").severity(JettraButton.Severity.HELP),
                JettraButton.of("Danger").severity(JettraButton.Severity.DANGER)
            ).modifier(new io.jettra.flux.core.Modifier().style("gap: 1rem; flex-wrap: wrap;"))
        )).modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 2rem;"));

        Widget outlinedButtons = Card.of(Column.of(
            Header.of(4, "Outlined Buttons").modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 1rem;")),
            Row.of(
                JettraButton.of("Primary").severity(JettraButton.Severity.PRIMARY).type(JettraButton.Type.OUTLINED),
                JettraButton.of("Secondary").severity(JettraButton.Severity.SECONDARY).type(JettraButton.Type.OUTLINED),
                JettraButton.of("Success").severity(JettraButton.Severity.SUCCESS).type(JettraButton.Type.OUTLINED),
                JettraButton.of("Info").severity(JettraButton.Severity.INFO).type(JettraButton.Type.OUTLINED),
                JettraButton.of("Warning").severity(JettraButton.Severity.WARNING).type(JettraButton.Type.OUTLINED),
                JettraButton.of("Help").severity(JettraButton.Severity.HELP).type(JettraButton.Type.OUTLINED),
                JettraButton.of("Danger").severity(JettraButton.Severity.DANGER).type(JettraButton.Type.OUTLINED)
            ).modifier(new io.jettra.flux.core.Modifier().style("gap: 1rem; flex-wrap: wrap;"))
        )).modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 2rem;"));

        Widget textButtons = Card.of(Column.of(
            Header.of(4, "Text Buttons").modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 1rem;")),
            Row.of(
                JettraButton.of("Primary").severity(JettraButton.Severity.PRIMARY).type(JettraButton.Type.TEXT)
            ).modifier(new io.jettra.flux.core.Modifier().style("gap: 1rem; flex-wrap: wrap;"))
        )).modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 2rem;"));

        Widget roundedButtons = Card.of(Column.of(
            Header.of(4, "Rounded Buttons").modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 1rem;")),
            Row.of(
                JettraButton.of("Primary", io.jettra.flux.widgets.Icon.Heroicons.HOME).severity(JettraButton.Severity.PRIMARY).rounded(),
                JettraButton.of("Success", io.jettra.flux.widgets.Icon.CHECK_CIRCLE).severity(JettraButton.Severity.SUCCESS).rounded(),
                JettraButton.of("Danger", io.jettra.flux.widgets.Icon.EXCLAMATION_TRIANGLE).severity(JettraButton.Severity.DANGER).rounded()
            ).modifier(new io.jettra.flux.core.Modifier().style("gap: 1rem; flex-wrap: wrap;"))
        )).modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 2rem;"));

        Widget formAction = Card.of(Column.of(
            Header.of(4, "Action Button (Secure)").modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 1rem;")),
            io.jettra.flux.widgets.Form.of(
                io.jettra.flux.widgets.Paragraph.of("<input type=\"hidden\" name=\"_action_method\" value=\"secureAction\">"),
                JettraButton.of("Execute Secure Action").severity(JettraButton.Severity.PRIMARY).actionMethod("secureAction")
            ).action(JettraServer.resolvePath("/button-demo")).method("POST")
        )).modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 2rem;"));

        boolean showModal = "true".equals(params.get("showModal"));

        Widget secureDialog = io.jettra.flux.widgets.Dialog.of(
            Header.of(4, "Success"),
            Paragraph.of("Action executed successfully using purely JettraFlux components!"),
            JettraButton.of("OK").severity(JettraButton.Severity.PRIMARY).modifier(new io.jettra.flux.core.Modifier().attribute("onclick", "window.location.href='" + JettraServer.resolvePath("/button-demo") + "'"))
        ).modifier(new io.jettra.flux.core.Modifier().style("padding: 20px; border-radius: 8px; border: none; box-shadow: 0 10px 15px rgba(0,0,0,0.1);")).attribute("id", "secureDialog");
        
        ((io.jettra.flux.widgets.Dialog)secureDialog).open(showModal);

        return Column.of(
            secureDialog,
            Paragraph.of(io.jettra.flux.theme.ButtonCSS.get()),
            Header.of(2, "Button Components").modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            solidButtons,
            outlinedButtons,
            textButtons,
            roundedButtons,
            formAction
        ).modifier(new io.jettra.flux.core.Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px;"));
    }

    @ActionWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER }, department = "")
    public void secureAction(HttpExchange exchange, Map<String, String> params) {
        try {
            redirect(exchange, "/button-demo?showModal=true");
        } catch (Exception e) {
        }
    }
}
