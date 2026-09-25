package com.flux.plugin.example.pages.uikit.uicomponents;

import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Card;
import io.jettra.flux.widgets.Header;
import io.jettra.flux.widgets.Paragraph;
import io.jettra.flux.widgets.Datatable;
import io.jettra.core.security.widget.PageWidgetAllow;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.flux.example.pages.template.TemplatePage;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role={jcf.AppRole.ADMIN, jcf.AppRole.MANAGER, jcf.AppRole.USER})
@io.jettra.core.server.Page(path = "/table")
public class TablePage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Table - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        


        // Table Data (Mocking Customers)
        List<Widget> headers = Arrays.asList(
            io.jettra.flux.widgets.Text.of("Name"), 
            io.jettra.flux.widgets.Text.of("Country"), 
            io.jettra.flux.widgets.Text.of("Agent"), 
            io.jettra.flux.widgets.Text.of("Date"), 
            io.jettra.flux.widgets.Text.of("Balance"), 
            io.jettra.flux.widgets.Text.of("Status"), 
            io.jettra.flux.widgets.Text.of("Activity")
        );

        List<List<Widget>> rows = Arrays.asList(
            Arrays.asList(
                io.jettra.flux.widgets.Text.of("James Butt").modifier(new io.jettra.flux.core.Modifier().style("font-weight: bold;")), 
                io.jettra.flux.widgets.Row.of(
                    io.jettra.flux.widgets.Image.of("https://flagcdn.com/24x18/us.png").alt("USA").modifier(new io.jettra.flux.core.Modifier().style("vertical-align:middle; margin-right:8px;")),
                    io.jettra.flux.widgets.Text.of("USA")
                ),
                io.jettra.flux.widgets.AvatarGroup.of(
                    io.jettra.flux.widgets.Image.of("https://randomuser.me/api/portraits/women/44.jpg").modifier(new io.jettra.flux.core.Modifier().cssClass("avatar-img")),
                    io.jettra.flux.widgets.Text.of("Ioni Bowcher")
                ),
                io.jettra.flux.widgets.Text.of("09/13/2025"), 
                io.jettra.flux.widgets.Text.of("$70,663.00").modifier(new io.jettra.flux.core.Modifier().style("font-weight: bold;")), 
                io.jettra.flux.widgets.Badge.of("QUALIFIED").severity("qualified"),
                io.jettra.flux.widgets.Row.of(
                    io.jettra.flux.widgets.ProgressBar.create().value(50).modifier(new io.jettra.flux.core.Modifier().style("width: 100px;")),
                    io.jettra.flux.widgets.Text.of("50%")
                ).modifier(new io.jettra.flux.core.Modifier().cssClass("activity-cell"))
            ),
            Arrays.asList(
                io.jettra.flux.widgets.Text.of("Josephine Darakjy").modifier(new io.jettra.flux.core.Modifier().style("font-weight: bold;")), 
                io.jettra.flux.widgets.Row.of(
                    io.jettra.flux.widgets.Image.of("https://flagcdn.com/24x18/es.png").alt("Spain").modifier(new io.jettra.flux.core.Modifier().style("vertical-align:middle; margin-right:8px;")),
                    io.jettra.flux.widgets.Text.of("Spain")
                ),
                io.jettra.flux.widgets.AvatarGroup.of(
                    io.jettra.flux.widgets.Image.of("https://randomuser.me/api/portraits/men/32.jpg").modifier(new io.jettra.flux.core.Modifier().cssClass("avatar-img")),
                    io.jettra.flux.widgets.Text.of("Amy Elsner")
                ),
                io.jettra.flux.widgets.Text.of("02/09/2025"), 
                io.jettra.flux.widgets.Text.of("$82,429.00").modifier(new io.jettra.flux.core.Modifier().style("font-weight: bold;")), 
                io.jettra.flux.widgets.Badge.of("NEW").severity("new"),
                io.jettra.flux.widgets.Row.of(
                    io.jettra.flux.widgets.ProgressBar.create().value(80).modifier(new io.jettra.flux.core.Modifier().style("width: 100px; accent-color:#22c55e;")),
                    io.jettra.flux.widgets.Text.of("80%")
                ).modifier(new io.jettra.flux.core.Modifier().cssClass("activity-cell"))
            ),
            Arrays.asList(
                io.jettra.flux.widgets.Text.of("Art Venere").modifier(new io.jettra.flux.core.Modifier().style("font-weight: bold;")), 
                io.jettra.flux.widgets.Row.of(
                    io.jettra.flux.widgets.Image.of("https://flagcdn.com/24x18/fr.png").alt("France").modifier(new io.jettra.flux.core.Modifier().style("vertical-align:middle; margin-right:8px;")),
                    io.jettra.flux.widgets.Text.of("France")
                ),
                io.jettra.flux.widgets.AvatarGroup.of(
                    io.jettra.flux.widgets.Image.of("https://randomuser.me/api/portraits/men/62.jpg").modifier(new io.jettra.flux.core.Modifier().cssClass("avatar-img")),
                    io.jettra.flux.widgets.Text.of("Asiya Javayant")
                ),
                io.jettra.flux.widgets.Text.of("05/13/2025"), 
                io.jettra.flux.widgets.Text.of("$10,934.00").modifier(new io.jettra.flux.core.Modifier().style("font-weight: bold;")), 
                io.jettra.flux.widgets.Badge.of("UNQUALIFIED").severity("unqualified"),
                io.jettra.flux.widgets.Row.of(
                    io.jettra.flux.widgets.ProgressBar.create().value(25).modifier(new io.jettra.flux.core.Modifier().style("width: 100px; accent-color:#ef4444;")),
                    io.jettra.flux.widgets.Text.of("25%")
                ).modifier(new io.jettra.flux.core.Modifier().cssClass("activity-cell"))
            ),
            Arrays.asList(
                io.jettra.flux.widgets.Text.of("Lenna Paprocki").modifier(new io.jettra.flux.core.Modifier().style("font-weight: bold;")), 
                io.jettra.flux.widgets.Row.of(
                    io.jettra.flux.widgets.Image.of("https://flagcdn.com/24x18/de.png").alt("Germany").modifier(new io.jettra.flux.core.Modifier().style("vertical-align:middle; margin-right:8px;")),
                    io.jettra.flux.widgets.Text.of("Germany")
                ),
                io.jettra.flux.widgets.AvatarGroup.of(
                    io.jettra.flux.widgets.Image.of("https://randomuser.me/api/portraits/women/68.jpg").modifier(new io.jettra.flux.core.Modifier().cssClass("avatar-img")),
                    io.jettra.flux.widgets.Text.of("Bernardo Dominic")
                ),
                io.jettra.flux.widgets.Text.of("09/15/2025"), 
                io.jettra.flux.widgets.Text.of("$500.00").modifier(new io.jettra.flux.core.Modifier().style("font-weight: bold;")), 
                io.jettra.flux.widgets.Badge.of("NEGOTIATION").severity("negotiation"),
                io.jettra.flux.widgets.Row.of(
                    io.jettra.flux.widgets.ProgressBar.create().value(59).modifier(new io.jettra.flux.core.Modifier().style("width: 100px; accent-color:#eab308;")),
                    io.jettra.flux.widgets.Text.of("59%")
                ).modifier(new io.jettra.flux.core.Modifier().cssClass("activity-cell"))
            )
        );

        Widget datatable = Datatable.ofWidgets(headers, rows);
        
        Widget tableCard = Card.of(Column.of(
            Header.of(4, "Customers").modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; font-weight: 600; font-size: 1.25rem; color: #1e293b;")),
            datatable
        )).modifier(new io.jettra.flux.core.Modifier().style("width: 100%; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);"));

        return Column.of(
            Paragraph.of(io.jettra.flux.theme.DataTableCSS.get()),
            Header.of(2, "Data Table").modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            tableCard
        ).modifier(new io.jettra.flux.core.Modifier().style("width: 100%; align-items: flex-start; max-width: 1200px; padding: 20px;"));
    }
}
