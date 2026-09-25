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
@io.jettra.core.server.Page(path = "/shopping-cart")
public class ShoppingCartPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Shopping Cart - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget cartItem1 = buildCartItem("Bamboo Watch", "Accessories", "$65.00", 1, "bamboo-watch.jpg");
        Widget cartItem2 = buildCartItem("Black Watch", "Accessories", "$72.00", 2, "black-watch.jpg");
        Widget cartItem3 = buildCartItem("Blue Band", "Fitness", "$79.00", 1, "blue-band.jpg");

        Widget cartList = Column.of(cartItem1, Divider.of(), cartItem2, Divider.of(), cartItem3)
            .modifier(new Modifier().style("flex: 2; padding: 25px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); border: 1px solid rgba(128,128,128,0.2); margin-right: 30px;"));

        Widget orderSummary = Card.of(Column.of(
            Header.of(4, "Order Summary").modifier(new Modifier().style("margin-top: 0; margin-bottom: 25px; color: var(--text-color); font-weight: 700;")),
            
            Row.of(Text.of("Subtotal").modifier(new Modifier().style("color: #64748b; font-weight: 600;")), Text.of("$288.00").modifier(new Modifier().style("color: var(--text-color); font-weight: 700;"))).modifier(new Modifier().style("justify-content: space-between; margin-bottom: 15px;")),
            Row.of(Text.of("Shipping").modifier(new Modifier().style("color: #64748b; font-weight: 600;")), Text.of("Free").modifier(new Modifier().style("color: var(--text-color); font-weight: 700;"))).modifier(new Modifier().style("justify-content: space-between; margin-bottom: 15px;")),
            Row.of(Text.of("Taxes").modifier(new Modifier().style("color: #64748b; font-weight: 600;")), Text.of("$23.04").modifier(new Modifier().style("color: var(--text-color); font-weight: 700;"))).modifier(new Modifier().style("justify-content: space-between; margin-bottom: 25px;")),
            
            Divider.of().modifier(new Modifier().style("margin-bottom: 25px;")),
            
            Row.of(Text.of("Total").modifier(new Modifier().style("color: var(--text-color); font-weight: 700; font-size: 1.1rem;")), Text.of("$311.04").modifier(new Modifier().style("color: var(--primary-color); font-weight: 700; font-size: 1.25rem;"))).modifier(new Modifier().style("justify-content: space-between; margin-bottom: 30px;")),
            
            ElevatedButton.of("Checkout").modifier(new Modifier().style("width: 100%; background: var(--primary-color); color: white; border: none; padding: 12px; border-radius: 6px; font-weight: 600; cursor: pointer; text-align: center;"))
        )).modifier(new Modifier().style("flex: 1; min-width: 300px; padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); border: 1px solid rgba(128,128,128,0.2); align-self: flex-start;"));

        Widget mainRow = Row.of(cartList, orderSummary).modifier(new Modifier().style("display: flex; flex-wrap: wrap; width: 100%;"));

        return Column.of(
            Header.of(2, "Shopping Cart").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 30px; color: var(--text-color);")),
            mainRow
        ).modifier(new Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px;"));
    }

    private Widget buildCartItem(String name, String category, String price, int quantity, String imgName) {
        return Row.of(
            Image.of("https://primefaces.org/cdn/primeng/images/demo/product/" + imgName).modifier(new Modifier().style("width: 100px; height: 100px; object-fit: cover; border-radius: 8px; border: 1px solid rgba(128,128,128,0.2); margin-right: 20px;")),
            Column.of(
                Header.of(5, name).modifier(new Modifier().style("margin: 0 0 5px 0; color: var(--text-color); font-weight: 700;")),
                Text.of(category).modifier(new Modifier().style("color: #64748b; font-size: 0.85rem; font-weight: 600; margin-bottom: 10px;")),
                Text.of(price).modifier(new Modifier().style("font-weight: 700; color: var(--text-color); font-size: 1.1rem;"))
            ).modifier(new Modifier().style("flex: 1;")),
            Row.of(
                InputNumber.of(quantity),
                Icon.of(Icon.TRASH_ALT).modifier(new Modifier().style("color: #ef4444; font-size: 1.2rem; cursor: pointer; margin-left: 20px;"))
            ).modifier(new Modifier().style("align-items: center;"))
        ).modifier(new Modifier().style("align-items: center; width: 100%; padding: 15px 0;"));
    }
}
