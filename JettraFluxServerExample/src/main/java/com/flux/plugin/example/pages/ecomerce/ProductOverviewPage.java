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
@io.jettra.core.server.Page(path = "/product-overview")
public class ProductOverviewPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Product Overview - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget imageGallery = Column.of(
            Image.of("https://primefaces.org/cdn/primeng/images/demo/product/bamboo-watch.jpg").modifier(new Modifier().style("width: 100%; border-radius: 12px; margin-bottom: 15px; border: 1px solid rgba(128,128,128,0.2);")),
            Row.of(
                Image.of("https://primefaces.org/cdn/primeng/images/demo/product/bamboo-watch.jpg").modifier(new Modifier().style("width: 23%; border-radius: 8px; border: 2px solid var(--primary-color); cursor: pointer;")),
                Image.of("https://primefaces.org/cdn/primeng/images/demo/product/bamboo-watch.jpg").modifier(new Modifier().style("width: 23%; border-radius: 8px; border: 1px solid rgba(128,128,128,0.2); opacity: 0.6; cursor: pointer;")),
                Image.of("https://primefaces.org/cdn/primeng/images/demo/product/bamboo-watch.jpg").modifier(new Modifier().style("width: 23%; border-radius: 8px; border: 1px solid rgba(128,128,128,0.2); opacity: 0.6; cursor: pointer;")),
                Image.of("https://primefaces.org/cdn/primeng/images/demo/product/bamboo-watch.jpg").modifier(new Modifier().style("width: 23%; border-radius: 8px; border: 1px solid rgba(128,128,128,0.2); opacity: 0.6; cursor: pointer;"))
            ).modifier(new Modifier().style("display: flex; justify-content: space-between; width: 100%;"))
        ).modifier(new Modifier().style("flex: 1; min-width: 400px; max-width: 50%; padding-right: 20px;"));

        Widget productInfo = Column.of(
            Row.of(
                Header.of(2, "Product Overview").modifier(new Modifier().style("margin: 0; font-weight: 700; color: var(--text-color);")),
                Badge.of("INSTOCK").modifier(new Modifier().style("background: #22c55e20; color: #22c55e; border-radius: 12px; padding: 4px 8px; font-weight: 700; font-size: 0.75rem; align-self: center; margin-left: 15px;"))
            ).modifier(new Modifier().style("align-items: center; margin-bottom: 10px;")),
            
            Row.of(
                Rating.of(4),
                Text.of("24 Reviews").modifier(new Modifier().style("color: var(--primary-color); font-weight: 600; margin-left: 10px; cursor: pointer;"))
            ).modifier(new Modifier().style("align-items: center; margin-bottom: 20px;")),
            
            Text.of("$120.00").modifier(new Modifier().style("font-size: 1.5rem; font-weight: 700; color: var(--text-color); margin-bottom: 20px;")),
            
            Text.of("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam nec orci est. Mauris varius urna sed hendrerit sodales. Sed nec massa nisl. Nullam sed vehicula libero, vel convallis lorem.").modifier(new Modifier().style("color: #64748b; margin-bottom: 25px; line-height: 1.6;")),
            
            Header.of(6, "Color").modifier(new Modifier().style("margin-bottom: 10px; color: var(--text-color); font-weight: 700;")),
            Row.of(
                Badge.of("").modifier(new Modifier().style("width: 30px; height: 30px; border-radius: 50%; background: #64748b; border: 2px solid var(--surface-color); box-shadow: 0 0 0 2px var(--primary-color); cursor: pointer;")),
                Badge.of("").modifier(new Modifier().style("width: 30px; height: 30px; border-radius: 50%; background: #3b82f6; cursor: pointer; margin-left: 10px;")),
                Badge.of("").modifier(new Modifier().style("width: 30px; height: 30px; border-radius: 50%; background: #10b981; cursor: pointer; margin-left: 10px;"))
            ).modifier(new Modifier().style("margin-bottom: 25px; align-items: center;")),
            
            Header.of(6, "Size").modifier(new Modifier().style("margin-bottom: 10px; color: var(--text-color); font-weight: 700;")),
            Row.of(
                Dropdown.of("Small", "Medium", "Large").placeholder("Select a Size").modifier(new Modifier().style("width: 250px;"))
            ).modifier(new Modifier().style("margin-bottom: 30px;")),
            
            Header.of(6, "Quantity").modifier(new Modifier().style("margin-bottom: 10px; color: var(--text-color); font-weight: 700;")),
            Row.of(
                InputNumber.of(1),
                ElevatedButton.of("Add to Cart").modifier(new Modifier().style("background: var(--primary-color); color: white; border: none; border-radius: 6px; padding: 10px 20px; font-weight: 600; cursor: pointer; margin-left: 15px; display: flex; align-items: center; gap: 8px;")),
                Icon.of(Icon.HEART).modifier(new Modifier().style("color: rgba(128,128,128,0.5); font-size: 1.2rem; cursor: pointer; margin-left: 15px;"))
            ).modifier(new Modifier().style("align-items: stretch; margin-bottom: 20px;"))
        ).modifier(new Modifier().style("flex: 1; min-width: 350px; padding-left: 20px; display: flex; flex-direction: column;"));

        Widget topSection = Row.of(imageGallery, productInfo).modifier(new Modifier().style("display: flex; flex-wrap: wrap; margin-bottom: 40px; background: var(--surface-color); padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid rgba(128,128,128,0.2);"));

        Widget tabContent1 = Column.of(
            Header.of(4, "Features").modifier(new Modifier().style("margin-top: 0; color: var(--text-color); font-weight: 700; margin-bottom: 15px;")),
            Text.of("Lorem ipsum dolor sit amet, consectetur adipiscing elit.").modifier(new Modifier().style("color: #64748b; margin-bottom: 10px;")),
            Row.of(Icon.of(Icon.CHECK_CIRCLE).modifier(new Modifier().style("color: #22c55e; margin-right: 10px;")), Text.of("Durable material").modifier(new Modifier().style("color: var(--text-color);"))).modifier(new Modifier().style("margin-bottom: 10px; align-items: center;")),
            Row.of(Icon.of(Icon.CHECK_CIRCLE).modifier(new Modifier().style("color: #22c55e; margin-right: 10px;")), Text.of("Water resistant").modifier(new Modifier().style("color: var(--text-color);"))).modifier(new Modifier().style("margin-bottom: 10px; align-items: center;")),
            Row.of(Icon.of(Icon.CHECK_CIRCLE).modifier(new Modifier().style("color: #22c55e; margin-right: 10px;")), Text.of("Long battery life").modifier(new Modifier().style("color: var(--text-color);"))).modifier(new Modifier().style("margin-bottom: 10px; align-items: center;"))
        ).modifier(new Modifier().style("padding: 20px;"));

        Widget tabContent2 = Column.of(
            Text.of("Customer Reviews will be displayed here.").modifier(new Modifier().style("color: #64748b; padding: 20px;"))
        );

        Widget tabsSection = TabView.of(
            TabPanel.of("Details", tabContent1),
            TabPanel.of("Reviews", tabContent2),
            TabPanel.of("Shipping and Returns", Text.of("Shipping info...").modifier(new Modifier().style("padding: 20px; color: #64748b;")))
        ).modifier(new Modifier().style("background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid rgba(128,128,128,0.2); overflow: hidden;"));

        return Column.of(
            topSection,
            tabsSection
        ).modifier(new Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px;"));
    }
}
