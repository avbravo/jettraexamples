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
@io.jettra.core.server.Page(path = "/overlay")
public class OverlayPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Overlay - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget dialogCard = Card.of(Column.of(
            Header.of(4, "Dialog").modifier(new Modifier().style("margin-bottom: 20px;")),
            Dialog.of(
                Header.of(4, "Simple Dialog"),
                Text.of("This is a dialog from JettraFlux component."),
                Button.of("Close").modifier(new Modifier().style("margin-top:20px; background:#ef4444; color:white; border:none; padding:8px 16px; border-radius:6px; cursor:pointer;").attribute("onclick", "this.closest('dialog').close()"))
            ).modifier(new Modifier().style("padding: 20px; border: none; border-radius: 8px; box-shadow: 0 10px 15px rgba(0,0,0,0.1);")).attribute("id", "myDialog"),
            Button.of("Show Dialog").modifier(new Modifier().style("background: #3b82f6; color: white; border-radius: 6px; padding: 10px 20px;").attribute("onclick", "document.getElementById('myDialog').showModal()"))
        )).modifier(new Modifier().style("padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); margin-bottom: 30px; width: 100%;"));

        Widget overlayPanelCard = Card.of(Column.of(
            Header.of(4, "Popover (OverlayPanel)").modifier(new Modifier().style("margin-bottom: 20px;")),
            Popover.of(
                Image.of("https://primefaces.org/cdn/primeng/images/product/bamboo-watch.jpg").modifier(new Modifier().style("width:150px; border-radius:8px;"))
            ).target(Button.of("Toggle Popover").modifier(new Modifier().style("background: #10b981; color: white; border-radius: 6px; padding: 10px 20px; margin-right: 20px;")))
        )).modifier(new Modifier().style("padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); margin-bottom: 30px; width: 100%;"));

        Widget drawerCard = Card.of(Column.of(
            Header.of(4, "Drawer (Sidebar)").modifier(new Modifier().style("margin-bottom: 20px;")),
            Row.of(
                Drawer.of(Text.of("Left Drawer Content")).position("left").trigger(Button.of("Left").modifier(new Modifier().style("background:var(--primary-color); color:white; border-radius:6px; padding:8px 16px;"))),
                Drawer.of(Text.of("Right Drawer Content")).position("right").trigger(Button.of("Right").modifier(new Modifier().style("background:var(--primary-color); color:white; border-radius:6px; padding:8px 16px; margin-left:10px;"))),
                Drawer.of(Text.of("Top Drawer Content")).position("top").trigger(Button.of("Top").modifier(new Modifier().style("background:var(--primary-color); color:white; border-radius:6px; padding:8px 16px; margin-left:10px;"))),
                Drawer.of(Text.of("Bottom Drawer Content")).position("bottom").trigger(Button.of("Bottom").modifier(new Modifier().style("background:var(--primary-color); color:white; border-radius:6px; padding:8px 16px; margin-left:10px;")))
            )
        )).modifier(new Modifier().style("padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); margin-bottom: 30px; width: 100%;"));

        Widget confirmCard = Card.of(Column.of(
            Header.of(4, "Confirm Popup & Dialog").modifier(new Modifier().style("margin-bottom: 20px;")),
            Row.of(
                ConfirmPopup.of(Button.of("Delete").modifier(new Modifier().style("background:#ef4444; color:white; border-radius:6px; padding:8px 16px; margin-right:15px;")), "Are you sure you want to proceed?"),
                ConfirmDialog.of(Button.of("Save").modifier(new Modifier().style("background:#10b981; color:white; border-radius:6px; padding:8px 16px;")), "Do you want to save the changes?")
            )
        )).modifier(new Modifier().style("padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); margin-bottom: 30px; width: 100%;"));

        Widget tooltipCard = Card.of(Column.of(
            Header.of(4, "Tooltip").modifier(new Modifier().style("margin-bottom: 20px;")),
            Row.of(
                Tooltip.of(Button.of("Hover Me").modifier(new Modifier().style("background:var(--surface-border); border-radius:6px; padding:8px 16px;")), "This is a native tooltip")
            )
        )).modifier(new Modifier().style("padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); margin-bottom: 30px; width: 100%;"));

        return Column.of(
            Header.of(2, "Overlay Components").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            dialogCard,
            overlayPanelCard,
            drawerCard,
            confirmCard,
            tooltipCard
        ).modifier(new Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px;"));
    }
}
