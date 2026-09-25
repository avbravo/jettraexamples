package com.flux.plugin.example.pages.layoutandgrid;

import com.flux.example.pages.template.TemplatePage;
import com.sun.net.httpserver.HttpExchange;
import io.jettra.core.security.widget.PageWidgetAllow;
import io.jettra.core.server.Page;
import io.jettra.flux.core.Widget;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;
import io.jettra.flux.widgets.Card;
import io.jettra.flux.widgets.Scaffold;
import io.jettra.flux.widgets.Top;
import io.jettra.flux.widgets.Label;
import io.jettra.flux.widgets.Paragraph;
import java.util.Map;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER, jcf.AppRole.USER })
@Page(path = "/card-demo")
public class CardDemoPage  extends TemplatePage {

 
  @Override
    protected String getTitle() {
        return "Card Demo";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        Widget content = Card.of(
            io.jettra.flux.widgets.Column.of(
                Label.of("Título del Card").modifier(new io.jettra.flux.core.Modifier().cssClass("bold-label")),
                Paragraph.of("Este es el contenido de un Card creado de manera programática en Java. No hay HTML duro aquí.")
            )
        ).modifier(new io.jettra.flux.core.Modifier().padding(15).cssClass("card-shadow"));
        
        return Scaffold.of()
            .topBar(Top.of(io.jettra.flux.widgets.Header.of(3, "Catálogo - Card")))
            .body(content);
    }
}
