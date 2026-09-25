package com.flux.plugin.example.pages.uikit.uicomponents;

import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Header;
import io.jettra.flux.widgets.Paragraph;
import io.jettra.flux.widgets.Message;
import io.jettra.core.security.widget.PageWidgetAllow;
import java.util.Map;
import com.flux.example.pages.template.TemplatePage;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER, jcf.AppRole.USER })
@io.jettra.core.server.Page(path = "/message")
public class MessagePage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Messages - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget messages = Column.of(
            Message.of(Message.Severity.SUCCESS, "Order submitted successfully!"),
            Message.of(Message.Severity.INFO, "A new software update is available."),
            Message.of(Message.Severity.WARNING, "Your subscription is about to expire."),
            Message.of(Message.Severity.ERROR, "Validation failed. Please check your inputs.")
        );

        return Column.of(
            Paragraph.of(io.jettra.flux.theme.MessageCSS.get()),
            Header.of(2, "Message Components").modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            messages
        ).modifier(new io.jettra.flux.core.Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px;"));
    }
}
