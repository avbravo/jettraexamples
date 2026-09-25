package com.flux.example.pages.dashboard;

import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Paragraph;
import com.flux.example.pages.template.TemplatePage;
import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import io.jettra.core.security.widget.PageWidgetAllow;
import java.util.Map;

@PageWidgetAllow(role={jcf.AppRole.ADMIN, jcf.AppRole.MANAGER})
@io.jettra.core.server.Page(path = "/dashboard")
public class DashboardPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Dashboard - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget customCss = Paragraph.of(io.jettra.flux.theme.OceanTheme.DashboardPage.CustomCSS);

        // --- Stats Row ---
        Widget stat1 = io.jettra.flux.widgets.StatCard.of("Conversion Rate", "0.8%", "0.81%", false);
        
        Widget stat2 = io.jettra.flux.widgets.StatCard.of("Avg. Order Value", "4.2%", "$306.2", true);
        
        Widget stat3 = io.jettra.flux.widgets.StatCard.of("Order Quantity", "2.1%", "1,620", false);

        
        // --- Main Row ---
        Widget mainChart = io.jettra.flux.widgets.VisitorGraphCard.of(
            "Unique Visitor Graph", "2025",
            "$620,076", "MRR GROWTH",
            "$1,120", "AVG. MRR/CUSTOMER",
            40, 60, 50, 80, 30, 75, 65, 95, 55
        );

        Widget transactions = io.jettra.flux.widgets.TransactionHistoryCard.of(
            "Transaction History",
            new io.jettra.flux.widgets.TransactionHistoryCard.TransactionItem(io.jettra.flux.widgets.Icon.CHECK, "#3b82f6", "Payment from #28492", "June 13, 2025 11:09 AM", "+$250.00", true),
            new io.jettra.flux.widgets.TransactionHistoryCard.TransactionItem(io.jettra.flux.widgets.Icon.REDO, "#ef4444", "Process refund to #94830", "June 13, 2025 08:22 AM", "-$570.00", false),
            new io.jettra.flux.widgets.TransactionHistoryCard.TransactionItem(io.jettra.flux.widgets.Icon.PLUS, "#22c55e", "New 8 user to #5849", "June 12, 2025 02:56 PM", "+$50.00", true),
            new io.jettra.flux.widgets.TransactionHistoryCard.TransactionItem(io.jettra.flux.widgets.Icon.CHECK, "#3b82f6", "Payment from #3382", "June 11, 2025 06:11 AM", "+$3830.00", true)
        );

        // Assemble structure manually using raw HTML strings since JettraFlux doesn't have a Grid widget
        Widget layout = Column.of(
            io.jettra.flux.widgets.Grid.of(stat1, stat2, stat3).modifier(new io.jettra.flux.core.Modifier().cssClass("oceantheme-dashboard-grid")),
            io.jettra.flux.widgets.Grid.of(mainChart, transactions).modifier(new io.jettra.flux.core.Modifier().cssClass("oceantheme-main-grid"))
        );

        // --- Center Content ---
        return Column.of(
            customCss,
            layout
        );
    }
}
