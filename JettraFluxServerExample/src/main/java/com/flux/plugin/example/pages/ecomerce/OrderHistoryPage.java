package com.flux.plugin.example.pages.ecomerce;

import io.jettra.flux.widgets.Icon;

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
@io.jettra.core.server.Page(path = "/order-history")
public class OrderHistoryPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Order History - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget order1 = buildOrderCard("Order #451234", "October 23, 2023", "$137.00", 
            buildOrderItem("Bamboo Watch", "Accessories", "Qty: 1", "$65.00", "bamboo-watch.jpg", "Delivered on Oct 25"),
            buildOrderItem("Black Watch", "Accessories", "Qty: 1", "$72.00", "black-watch.jpg", "Delivered on Oct 25")
        );

        Widget order2 = buildOrderCard("Order #451233", "October 10, 2023", "$29.00", 
            buildOrderItem("Blue T-Shirt", "Clothing", "Qty: 1", "$29.00", "blue-t-shirt.jpg", "Delivered on Oct 12")
        );

        return Column.of(
            Header.of(2, "Order History").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 30px; color: var(--text-color);")),
            order1,
            order2
        ).modifier(new Modifier().style("width: 100%; align-items: stretch; max-width: 1000px; padding: 20px;"));
    }

    private Widget buildOrderCard(String orderNum, String date, String total, Widget... items) {
        Widget headerRow = Row.of(
            Column.of(
                Text.of("Order Number").modifier(new Modifier().style("color: #64748b; font-size: 0.85rem; font-weight: 600; margin-bottom: 5px;")),
                Text.of(orderNum).modifier(new Modifier().style("color: var(--text-color); font-weight: 700; font-size: 1.1rem;"))
            ).modifier(new Modifier().style("margin-right: 40px;")),
            
            Column.of(
                Text.of("Order Date").modifier(new Modifier().style("color: #64748b; font-size: 0.85rem; font-weight: 600; margin-bottom: 5px;")),
                Text.of(date).modifier(new Modifier().style("color: var(--text-color); font-weight: 700; font-size: 1.1rem;"))
            ).modifier(new Modifier().style("margin-right: 40px;")),
            
            Column.of(
                Text.of("Total Amount").modifier(new Modifier().style("color: #64748b; font-size: 0.85rem; font-weight: 600; margin-bottom: 5px;")),
                Text.of(total).modifier(new Modifier().style("color: var(--primary-color); font-weight: 700; font-size: 1.1rem;"))
            ).modifier(new Modifier().style("flex: 1;")),
            
            Row.of(
                OutlinedButton.of("View Invoice").modifier(new Modifier().style("border-color: rgba(128,128,128,0.3); color: var(--text-color); padding: 8px 16px; border-radius: 6px; font-weight: 600; cursor: pointer; margin-right: 15px;"))
            )
        ).modifier(new Modifier().style("width: 100%; justify-content: space-between; align-items: flex-end; padding-bottom: 20px; border-bottom: 1px solid rgba(128,128,128,0.2); margin-bottom: 20px;"));

        Widget itemsList = Column.of(items).modifier(new Modifier().style("width: 100%;"));

        return Card.of(Column.of(
            headerRow,
            itemsList
        )).modifier(new Modifier().style("width: 100%; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid rgba(128,128,128,0.2); padding: 30px; margin-bottom: 30px;"));
    }

    private Widget buildOrderItem(String name, String category, String qty, String price, String imgName, String statusStr) {
        return Column.of(
            Row.of(
                Image.of("https://primefaces.org/cdn/primeng/images/demo/product/" + imgName).modifier(new Modifier().style("width: 100px; height: 100px; object-fit: cover; border-radius: 8px; border: 1px solid rgba(128,128,128,0.2); margin-right: 25px;")),
                
                Column.of(
                    Header.of(5, name).modifier(new Modifier().style("margin: 0 0 5px 0; color: var(--text-color); font-weight: 700;")),
                    Text.of(category).modifier(new Modifier().style("color: #64748b; font-size: 0.85rem; font-weight: 600; margin-bottom: 10px;")),
                    Text.of(qty).modifier(new Modifier().style("color: #64748b; font-size: 0.85rem; font-weight: 600; margin-bottom: 10px;"))
                ).modifier(new Modifier().style("flex: 2;")),
                
                Column.of(
                    Text.of(price).modifier(new Modifier().style("font-weight: 700; color: var(--text-color); font-size: 1.1rem; text-align: right; margin-bottom: 15px;")),
                    ElevatedButton.of("Buy Again").modifier(new Modifier().style("background: var(--surface-color); color: var(--text-color); border: 1px solid rgba(128,128,128,0.2); padding: 8px 16px; border-radius: 6px; font-weight: 600; cursor: pointer;"))
                ).modifier(new Modifier().style("flex: 1; align-items: flex-end; justify-content: center;"))
                
            ).modifier(new Modifier().style("width: 100%; align-items: flex-start; margin-bottom: 25px;")),
            
            Row.of(
                Icon.of(Icon.TRUCK).modifier(new Modifier().style("color: #22c55e; margin-right: 15px; font-size: 1.2rem;")),
                Text.of(statusStr).modifier(new Modifier().style("color: var(--text-color); font-weight: 600; font-size: 0.95rem;"))
            ).modifier(new Modifier().style("align-items: center; width: 100%; background: #22c55e10; padding: 15px 20px; border-radius: 8px; margin-bottom: 25px;")),
            
            Divider.of().modifier(new Modifier().style("margin-bottom: 25px; display: block;"))
        ).modifier(new Modifier().style("width: 100%;"));
    }
}
