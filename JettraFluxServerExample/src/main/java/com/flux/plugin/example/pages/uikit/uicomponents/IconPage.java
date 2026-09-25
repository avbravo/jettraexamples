/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flux.plugin.example.pages.uikit.uicomponents;
import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import java.util.Map;
import com.flux.example.pages.template.TemplatePage;

import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Paragraph;
import io.jettra.flux.widgets.Card;
import io.jettra.flux.widgets.Header;
import io.jettra.flux.widgets.Row;
import io.jettra.core.security.widget.PageWidgetAllow;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;
import io.jettra.flux.widgets.Icon;
/**
 *
 * @author avbravo
 */
@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER, jcf.AppRole.USER })
@io.jettra.core.server.Page(path = "/icon")
public class IconPage extends TemplatePage{

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        Widget solidButtons = Card.of(Column.of(
            Header.of(4, "Solid Buttons").modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 1rem;")),
            Row.of(
                    Icon.of(Icon.BOX),
                    Icon.of(Icon.BARS)
            
            ).modifier(new io.jettra.flux.core.Modifier().style("gap: 1rem; flex-wrap: wrap;"))
        )).modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 2rem;"));
        
     return Column.of(
            Paragraph.of(io.jettra.flux.theme.ButtonCSS.get()),
            Header.of(2, "Icon Page").modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            solidButtons
         
        ).modifier(new io.jettra.flux.core.Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px;"));
    
}
}
