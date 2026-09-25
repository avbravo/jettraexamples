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
@io.jettra.core.server.Page(path = "/product-list")
public class ProductListPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Product List - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget filters = Card.of(Column.of(
            Header.of(6, "Category").modifier(new Modifier().style("margin-bottom: 10px; color: var(--text-color); font-weight: 700; text-transform: uppercase; letter-spacing: 1px; font-size: 0.75rem;")),
            Row.of(Checkbox.of("acc"), Text.of("Accessories").modifier(new Modifier().style("margin-left: 10px; color: var(--text-color);"))).modifier(new Modifier().style("margin-bottom: 10px; align-items: center;")),
            Row.of(Checkbox.of("fit"), Text.of("Fitness").modifier(new Modifier().style("margin-left: 10px; color: var(--text-color);"))).modifier(new Modifier().style("margin-bottom: 10px; align-items: center;")),
            Row.of(Checkbox.of("clo"), Text.of("Clothing").modifier(new Modifier().style("margin-left: 10px; color: var(--text-color);"))).modifier(new Modifier().style("margin-bottom: 30px; align-items: center;")),
            
            Header.of(6, "Price Range").modifier(new Modifier().style("margin-bottom: 10px; color: var(--text-color); font-weight: 700; text-transform: uppercase; letter-spacing: 1px; font-size: 0.75rem;")),
            Row.of(RadioButton.of("p1", "price"), Text.of("$0 - $50").modifier(new Modifier().style("margin-left: 10px; color: var(--text-color);"))).modifier(new Modifier().style("margin-bottom: 10px; align-items: center;")),
            Row.of(RadioButton.of("p2", "price"), Text.of("$50 - $100").modifier(new Modifier().style("margin-left: 10px; color: var(--text-color);"))).modifier(new Modifier().style("margin-bottom: 10px; align-items: center;")),
            Row.of(RadioButton.of("p3", "price"), Text.of("$100+").modifier(new Modifier().style("margin-left: 10px; color: var(--text-color);"))).modifier(new Modifier().style("margin-bottom: 30px; align-items: center;"))
        )).modifier(new Modifier().style("width: 250px; padding: 25px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); border: 1px solid rgba(128,128,128,0.2); margin-right: 30px; flex-shrink: 0;"));

        Widget grid = Row.of(
            buildProduct("Bamboo Watch", "$65.00", "Accessories", "INSTOCK", "bamboo-watch.jpg"),
            buildProduct("Black Watch", "$72.00", "Accessories", "INSTOCK", "black-watch.jpg"),
            buildProduct("Blue Band", "$79.00", "Fitness", "LOWSTOCK", "blue-band.jpg"),
            buildProduct("Blue T-Shirt", "$29.00", "Clothing", "INSTOCK", "blue-t-shirt.jpg"),
            buildProduct("Bracelet", "$15.00", "Accessories", "INSTOCK", "bracelet.jpg"),
            buildProduct("Brown Purse", "$120.00", "Accessories", "OUTOFSTOCK", "brown-purse.jpg"),
            buildProduct("Chakra Bracelet", "$32.00", "Accessories", "LOWSTOCK", "chakra-bracelet.jpg"),
            buildProduct("Galaxy Earrings", "$34.00", "Accessories", "INSTOCK", "galaxy-earrings.jpg"),
            buildProduct("Game Controller", "$99.00", "Electronics", "LOWSTOCK", "game-controller.jpg")
        ).modifier(new Modifier().style("display: flex; flex-wrap: wrap; gap: 24px; flex: 1; align-content: flex-start; margin: 0;"));

        Widget mainContent = Row.of(filters, grid).modifier(new Modifier().style("align-items: flex-start; width: 100%; display: flex;"));

        Widget topToolbar = Row.of(
            Dropdown.of("Sort by Price", "Price: Low to High", "Price: High to Low").placeholder("Sort By..."),
            Row.of(
                Icon.of(Icon.TH_LARGE).modifier(new Modifier().style("color: var(--primary-color); font-size: 1.2rem; cursor: pointer; margin-right: 15px;")),
                Icon.of(Icon.BARS).modifier(new Modifier().style("color: rgba(128,128,128,0.5); font-size: 1.2rem; cursor: pointer;"))
            ).modifier(new Modifier().style("align-items: center;"))
        ).modifier(new Modifier().style("justify-content: space-between; align-items: center; width: 100%; padding: 15px 25px; border-radius: 12px; background: var(--surface-color); border: 1px solid rgba(128,128,128,0.2); margin-bottom: 25px; box-shadow: 0 2px 4px rgba(0,0,0,0.02);"));

        return Column.of(
            Header.of(2, "Product List").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px; color: var(--text-color);")),
            topToolbar,
            mainContent
        ).modifier(new Modifier().style("width: 100%; align-items: stretch; padding: 20px;"));
    }

    private Widget buildProduct(String name, String price, String category, String status, String imgName) {
        String badgeColor = status.equals("INSTOCK") ? "#22c55e" : status.equals("LOWSTOCK") ? "#eab308" : "#ef4444";
        
        return Card.of(Column.of(
            Row.of(
                Badge.of(status).modifier(new Modifier().style("background: " + badgeColor + "20; color: " + badgeColor + "; border-radius: 12px; padding: 4px 8px; font-weight: 700; font-size: 0.75rem;")),
                Icon.of(Icon.SHOPPING_CART).modifier(new Modifier().style("color: rgba(128,128,128,0.5); font-size: 1.1rem; cursor: pointer;"))
            ).modifier(new Modifier().style("justify-content: space-between; align-items: center; width: 100%; margin-bottom: 15px;")),
            
            Image.of("https://primefaces.org/cdn/primeng/images/demo/product/" + imgName).modifier(new Modifier().style("width: 100%; max-height: 200px; object-fit: contain; align-self: center; margin-bottom: 15px; border-radius: 8px;")),
            
            Text.of(category).modifier(new Modifier().style("color: #64748b; font-size: 0.85rem; margin-bottom: 5px; font-weight: 600;")),
            Text.of(name).modifier(new Modifier().style("font-weight: 700; color: var(--text-color); font-size: 1.15rem; margin-bottom: 15px;")),
            
            Row.of(
                Text.of(price).modifier(new Modifier().style("font-weight: 700; color: var(--text-color); font-size: 1.25rem;")),
                Rating.of(4)
            ).modifier(new Modifier().style("justify-content: space-between; align-items: center; width: 100%;"))
        )).modifier(new Modifier().style("width: calc(33.333% - 16px); min-width: 250px; padding: 20px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); border: 1px solid rgba(128,128,128,0.2); cursor: pointer; transition: transform 0.2s, box-shadow 0.2s;"));
    }
}
