package com.flux.plugin.example.pages.uikit.uicomponents;

import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Row;
import io.jettra.flux.widgets.Card;
import io.jettra.flux.widgets.Header;
import io.jettra.flux.widgets.Paragraph;
import io.jettra.flux.widgets.RawHtml;
import io.jettra.flux.widgets.DataView;
import io.jettra.flux.widgets.Image;
import io.jettra.flux.widgets.ElevatedButton;
import io.jettra.flux.widgets.Text;
import io.jettra.flux.widgets.Icon;
import io.jettra.core.security.widget.PageWidgetAllow;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.flux.example.pages.template.TemplatePage;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER, jcf.AppRole.USER })
@io.jettra.core.server.Page(path = "/dataview")
public class DataViewPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "DataView - JettraFlux Pro";
    }

    private Widget buildProductItem(String name, String description, String price, String category, String status,
            String imagePath) {
        // Build the product image
        Widget img = Image.of(imagePath).modifier(new io.jettra.flux.core.Modifier().style(
                "width: 100px; height: 100px; max-width: 100px; max-height: 100px; min-width: 100px; flex-shrink: 0; object-fit: cover; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);"));

        // Build product details
        Widget details = Column.of(
                Header.of(4, name)
                        .modifier(new io.jettra.flux.core.Modifier()
                                .style("margin: 0 0 5px 0; font-weight: 700; color: #1e293b;")),
                Paragraph.of(description)
                        .modifier(new io.jettra.flux.core.Modifier()
                                .style("margin: 0 0 10px 0; color: #64748b; font-size: 0.9rem;")),
                Row.of(
                        Icon.of(Icon.Heroicons.HOME)
                                .modifier(new io.jettra.flux.core.Modifier()
                                        .style("width: 16px; height: 16px; margin-right: 5px; color: #94a3b8;")),
                        Text.of(category)
                                .modifier(new io.jettra.flux.core.Modifier()
                                        .style("color: #64748b; font-size: 0.85rem; font-weight: 600;")))
                        .modifier(
                                new io.jettra.flux.core.Modifier().style("align-items: center; margin-bottom: 15px;")));

        // Build price and action
        Widget actionBox = Column.of(
                Text.of(price)
                        .modifier(new io.jettra.flux.core.Modifier()
                                .style("font-size: 1.5rem; font-weight: 700; color: #1e293b; margin-bottom: 10px;")),
                ElevatedButton.of("Add to Cart").modifier(new io.jettra.flux.core.Modifier().style(
                        "background-color: #3b82f6; color: white; padding: 8px 16px; border: none; border-radius: 6px; font-weight: 600; cursor: pointer; display: flex; align-items: center; gap: 8px;")))
                .modifier(new io.jettra.flux.core.Modifier().style("align-items: flex-end;"));

        // Combine into a layout suitable for the DataView item
        return Row.of(
                img,
                details.modifier(new io.jettra.flux.core.Modifier().style("flex: 1; margin-left: 20px;")),
                actionBox)
                .modifier(new io.jettra.flux.core.Modifier()
                        .style("width: 100%; align-items: center; justify-content: space-between;"));
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {

        List<Widget> products = Arrays.asList(
                buildProductItem("Bamboo Watch", "Product Description", "$65.00", "Accessories", "INSTOCK",
                        "https://primefaces.org/cdn/primeng/images/demo/product/bamboo-watch.jpg"),
                buildProductItem("Black Watch", "Product Description", "$72.00", "Accessories", "INSTOCK",
                        "https://primefaces.org/cdn/primeng/images/demo/product/black-watch.jpg"),
                buildProductItem("Blue Band", "Product Description", "$79.00", "Fitness", "LOWSTOCK",
                        "https://primefaces.org/cdn/primeng/images/demo/product/blue-band.jpg"),
                buildProductItem("Blue T-Shirt", "Product Description", "$29.00", "Clothing", "INSTOCK",
                        "https://primefaces.org/cdn/primeng/images/demo/product/blue-t-shirt.jpg"));

        Widget listHeader = Row.of(
                Header.of(3, "DataView List").modifier(
                        new io.jettra.flux.core.Modifier().style("margin: 0; font-weight: 600; color: #1e293b;")))
                .modifier(new io.jettra.flux.core.Modifier()
                        .style("width: 100%; justify-content: space-between; align-items: center;"));

        Widget dataViewList = DataView.of(products)
                .layout(DataView.Layout.LIST)
                .header(listHeader);

        Widget dataViewCard = Card.of(
                dataViewList)
                .modifier(new io.jettra.flux.core.Modifier().style(
                        "width: 100%; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); padding: 0;"));

        return Column.of(
                RawHtml.of(io.jettra.flux.theme.DataViewCSS.get()),
                Header.of(2, "DataView Component")
                        .modifier(new io.jettra.flux.core.Modifier()
                                .style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
                dataViewCard)
                .modifier(new io.jettra.flux.core.Modifier()
                        .style("width: 100%; align-items: flex-start; max-width: 1200px; padding: 20px;"));
    }
}
