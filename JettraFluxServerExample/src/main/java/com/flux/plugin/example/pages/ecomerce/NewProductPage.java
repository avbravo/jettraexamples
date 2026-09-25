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
@io.jettra.core.server.Page(path = "/new-product")
public class NewProductPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "New Product - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget productDetailsCard = Card.of(Column.of(
            Header.of(5, "Product Details").modifier(new Modifier().style("margin-top: 0; margin-bottom: 25px; color: var(--text-color); font-weight: 700;")),
            
            Column.of(
                Label.of("Product Name").modifier(new Modifier().style("margin-bottom: 8px; font-weight: 600; color: var(--text-color); font-size: 0.9rem;")),
                TextBox.of("").modifier(new Modifier().style("width: 100%; padding: 12px; border-radius: 6px; border: 1px solid rgba(128,128,128,0.2); background: var(--background-color); color: var(--text-color);"))
            ).modifier(new Modifier().style("margin-bottom: 20px;")),
            
            Column.of(
                Label.of("Description").modifier(new Modifier().style("margin-bottom: 8px; font-weight: 600; color: var(--text-color); font-size: 0.9rem;")),
                Editor.of("").modifier(new Modifier().style("width: 100%;"))
            ).modifier(new Modifier().style("margin-bottom: 20px;")),
            
            Row.of(
                Column.of(
                    Label.of("Price").modifier(new Modifier().style("margin-bottom: 8px; font-weight: 600; color: var(--text-color); font-size: 0.9rem;")),
                    TextBox.of("0.00").modifier(new Modifier().style("width: 100%; padding: 12px; border-radius: 6px; border: 1px solid rgba(128,128,128,0.2); background: var(--background-color); color: var(--text-color);"))
                ).modifier(new Modifier().style("flex: 1; margin-right: 15px;")),
                
                Column.of(
                    Label.of("Category").modifier(new Modifier().style("margin-bottom: 8px; font-weight: 600; color: var(--text-color); font-size: 0.9rem;")),
                    Dropdown.of("Accessories", "Clothing", "Electronics", "Fitness").placeholder("Select Category").modifier(new Modifier().style("width: 100%;"))
                ).modifier(new Modifier().style("flex: 1; margin-left: 15px;"))
            ).modifier(new Modifier().style("margin-bottom: 20px; display: flex; width: 100%;")),
            
            Row.of(
                Text.of("In Stock").modifier(new Modifier().style("font-weight: 600; color: var(--text-color); font-size: 0.9rem; margin-right: 15px;")),
                ToggleSwitch.of()
            ).modifier(new Modifier().style("align-items: center; margin-bottom: 20px;"))
            
        )).modifier(new Modifier().style("flex: 2; min-width: 400px; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid rgba(128,128,128,0.2); padding: 30px;"));


        Widget uploadSection = Column.of(
            Icon.of(Icon.CLOUD_UPLOAD_ALT).modifier(new Modifier().style("font-size: 3rem; color: #94a3b8; margin-bottom: 15px;")),
            Text.of("Drag and drop your product image here").modifier(new Modifier().style("color: #64748b; font-weight: 600; margin-bottom: 10px;")),
            Text.of("or").modifier(new Modifier().style("color: #94a3b8; font-size: 0.85rem; margin-bottom: 15px;")),
            OutlinedButton.of("Browse Files").modifier(new Modifier().style("border-color: var(--primary-color); color: var(--primary-color); padding: 8px 16px; border-radius: 6px; font-weight: 600; cursor: pointer;"))
        ).modifier(new Modifier().style("align-items: center; justify-content: center; width: 100%; height: 250px; border: 2px dashed rgba(128,128,128,0.3); border-radius: 8px; background: rgba(128,128,128,0.02); cursor: pointer; transition: background 0.2s;"));

        Widget imagesCard = Card.of(Column.of(
            Header.of(5, "Product Images").modifier(new Modifier().style("margin-top: 0; margin-bottom: 25px; color: var(--text-color); font-weight: 700;")),
            uploadSection
        )).modifier(new Modifier().style("flex: 1; min-width: 300px; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid rgba(128,128,128,0.2); padding: 30px;"));


        Widget mainRow = Row.of(productDetailsCard, imagesCard).modifier(new Modifier().style("display: flex; flex-wrap: wrap; gap: 30px; width: 100%; margin-bottom: 30px;"));

        Widget footerButtons = Row.of(
            OutlinedButton.of("Cancel").modifier(new Modifier().style("border-color: rgba(128,128,128,0.3); color: var(--text-color); padding: 10px 25px; border-radius: 6px; font-weight: 600; cursor: pointer; margin-right: 15px;")),
            ElevatedButton.of("Save Product").modifier(new Modifier().style("background: var(--primary-color); color: white; border: none; padding: 10px 25px; border-radius: 6px; font-weight: 600; cursor: pointer;"))
        ).modifier(new Modifier().style("justify-content: flex-end; width: 100%;"));

        return Column.of(
            Row.of(
                Header.of(2, "Create Product").modifier(new Modifier().style("margin-top: 0; font-weight: 700; color: var(--text-color);")),
                Row.of(
                    Icon.of(Icon.COG).modifier(new Modifier().style("color: rgba(128,128,128,0.5); font-size: 1.2rem; cursor: pointer;"))
                )
            ).modifier(new Modifier().style("justify-content: space-between; align-items: center; width: 100%; margin-bottom: 30px;")),
            mainRow,
            footerButtons
        ).modifier(new Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px;"));
    }
}
