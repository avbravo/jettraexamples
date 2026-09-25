package com.flux.plugin.example.pages.layoutandgrid;

import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import io.jettra.flux.widgets.Grid;
import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Card;
import io.jettra.flux.widgets.Header;
import io.jettra.flux.widgets.Paragraph;
import io.jettra.server.config.ConfigInjector;
import io.jettra.server.config.JettraConfigProperty;
import io.jettra.core.security.widget.PageWidgetAllow;
import java.util.Map;
import com.flux.example.pages.template.TemplatePage;
import io.jettra.core.server.Page;

@PageWidgetAllow(role={jcf.AppRole.ADMIN, jcf.AppRole.MANAGER, jcf.AppRole.USER})
@Page(path = "/grid-demo")
public class GridDemoPage extends TemplatePage {

    @JettraConfigProperty(name = "app.title")
    private String appTitle;

    public GridDemoPage() {
        ConfigInjector.inject(this);
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        String titleDisplay = (appTitle != null) ? appTitle : "JettraFlux Example";
        
        Widget header = Header.of(2, "Grid Layout Demo");
        Widget propInfo = Paragraph.of("Application Title from properties: <b>" + titleDisplay + "</b>");

        Widget card1 = Card.of(Column.of(Header.of(4, "Card 1"), Paragraph.of("This is the first card in the grid.")));
        Widget card2 = Card.of(Column.of(Header.of(4, "Card 2"), Paragraph.of("This is the second card in the grid.")));
        Widget card3 = Card.of(Column.of(Header.of(4, "Card 3"), Paragraph.of("This is the third card in the grid.")));
        Widget card4 = Card.of(Column.of(Header.of(4, "Card 4"), Paragraph.of("This is the fourth card in the grid.")));

        // Creating a 2x2 grid
        Widget grid = Grid.of(
            card1, card2,
            card3, card4
        ).modifier(new io.jettra.flux.core.Modifier().style("grid-template-columns: 1fr 1fr; gap: 20px;"));

        return Column.of(header, propInfo, grid).modifier(new io.jettra.flux.core.Modifier().style("gap: 20px;"));
    }
}
