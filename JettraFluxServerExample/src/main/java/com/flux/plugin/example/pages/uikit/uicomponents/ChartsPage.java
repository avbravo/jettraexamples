package com.flux.plugin.example.pages.uikit.uicomponents;

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
@io.jettra.core.server.Page(path = "/charts")
public class ChartsPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Charts - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget lineChart = Card.of(Column.of(
            Header.of(5, "Line Chart").modifier(new Modifier().style("margin-top: 0; margin-bottom: 1.5rem; color: var(--text-color); font-weight: 600;")),
            ChartsLine.of("lineChart")
        )).modifier(new Modifier().style("padding: 2rem; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); flex: 1; min-width: 300px; margin-bottom: 20px;"));

        Widget barChart = Card.of(Column.of(
            Header.of(5, "Bar Chart").modifier(new Modifier().style("margin-top: 0; margin-bottom: 1.5rem; color: var(--text-color); font-weight: 600;")),
            CharsBar.of("barChart")
        )).modifier(new Modifier().style("padding: 2rem; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); flex: 1; min-width: 300px; margin-bottom: 20px;"));
        
        Widget pieChart = Card.of(Column.of(
            Header.of(5, "Pie Chart").modifier(new Modifier().style("margin-top: 0; margin-bottom: 1.5rem; color: var(--text-color); font-weight: 600;")),
            CharsPie.of("pieChart")
        )).modifier(new Modifier().style("padding: 2rem; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); flex: 1; min-width: 300px; margin-bottom: 20px; display: flex; flex-direction: column; align-items: center; justify-content: center;"));

        Widget doughnutChart = Card.of(Column.of(
            Header.of(5, "Doughnut Chart").modifier(new Modifier().style("margin-top: 0; margin-bottom: 1.5rem; color: var(--text-color); font-weight: 600;")),
            CharsDoughnut.of("doughnutChart")
        )).modifier(new Modifier().style("padding: 2rem; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); flex: 1; min-width: 300px; margin-bottom: 20px; display: flex; flex-direction: column; align-items: center; justify-content: center;"));

        Widget row1 = Row.of(lineChart, barChart).modifier(new Modifier().style("gap: 20px; flex-wrap: wrap; display: flex; width: 100%;"));
        Widget row2 = Row.of(pieChart, doughnutChart).modifier(new Modifier().style("gap: 20px; flex-wrap: wrap; display: flex; width: 100%;"));

        return Column.of(
            Header.of(2, "Charts Component").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            row1,
            row2
        ).modifier(new Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px;"));
    }
}
