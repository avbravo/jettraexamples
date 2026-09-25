package com.flux.plugin.example.pages.apps;

import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import io.jettra.flux.core.Modifier;
import io.jettra.flux.widgets.*;
import io.jettra.core.security.widget.PageWidgetAllow;
import java.util.Map;
import com.flux.example.pages.template.TemplatePage;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER, jcf.AppRole.USER })
@io.jettra.core.server.Page(path = "/file")
public class FilePage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "File Upload - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget basicUpload = Card.of(Column.of(
            Header.of(4, "Basic Upload").modifier(new Modifier().style("margin-bottom: 20px;")),
            FileUpload.of("uploadBtn", "Select File...").modifier(new Modifier().style("margin-bottom: 10px;"))
        )).modifier(new Modifier().style("padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-card); margin-bottom: 30px; width: 100%;"));

        Widget advancedUpload = Card.of(Column.of(
            Header.of(4, "Advanced Upload").modifier(new Modifier().style("margin-bottom: 20px;")),
            Column.of(
                Icon.of(Icon.Heroicons.CLOUD_UPLOAD).modifier(new Modifier().style("width: 48px; height: 48px; color: #64748b; margin-bottom: 15px;")),
                Text.of("Drag and drop files to here to upload.").modifier(new Modifier().style("color: #64748b; font-size: 1.1rem;"))
            ).modifier(new Modifier().style("border: 2px dashed #cbd5e1; border-radius: 8px; padding: 40px; align-items: center; justify-content: center; background: #f8fafc; cursor: pointer;"))
        )).modifier(new Modifier().style("padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-card); margin-bottom: 30px; width: 100%;"));

        return Column.of(
            Header.of(2, "File Upload").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            basicUpload,
            advancedUpload
        ).modifier(new Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px;"));
    }
}
