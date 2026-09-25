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
@io.jettra.core.server.Page(path = "/tree")
public class TreePage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Tree - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        // Simulating a tree structure using nested Rows/Columns and Icons
        Widget treeNode1 = Column.of(
            buildTreeNode("Documents", Icon.Heroicons.FOLDER, true),
            Column.of(
                buildTreeNode("Work", Icon.Heroicons.FOLDER, false),
                buildTreeNode("Home", Icon.Heroicons.FOLDER, false)
            ).modifier(new Modifier().style("margin-left: 20px; border-left: 1px dashed #cbd5e1;"))
        );

        Widget treeNode2 = Column.of(
            buildTreeNode("Pictures", Icon.Heroicons.FOLDER, true),
            Column.of(
                buildTreeNode("Barcelona.jpg", Icon.Heroicons.PHOTOGRAPH, false),
                buildTreeNode("Logo.png", Icon.Heroicons.PHOTOGRAPH, false),
                buildTreeNode("Profile.jpg", Icon.Heroicons.PHOTOGRAPH, false)
            ).modifier(new Modifier().style("margin-left: 20px; border-left: 1px dashed #cbd5e1;"))
        );

        Widget treeCard = Card.of(Column.of(
            Header.of(4, "Basic Tree").modifier(new Modifier().style("margin-bottom: 20px;")),
            treeNode1,
            treeNode2
        )).modifier(new Modifier().style("padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: white; width: 400px;"));

        return Column.of(
            Header.of(2, "Tree Component").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            treeCard
        ).modifier(new Modifier().style("width: 100%; align-items: flex-start; max-width: 1200px; padding: 20px;"));
    }

    private Widget buildTreeNode(String label, String icon, boolean hasChildren) {
        return Row.of(
            Icon.of(hasChildren ? Icon.Heroicons.CHEVRON_DOWN : Icon.Heroicons.MINUS).modifier(new Modifier().style("width: 16px; height: 16px; margin-right: 5px; color: #94a3b8; cursor: pointer;")),
            Icon.of(icon).modifier(new Modifier().style("width: 20px; height: 20px; margin-right: 8px; color: #eab308;")),
            Text.of(label).modifier(new Modifier().style("font-weight: 500; color: #1e293b;"))
        ).modifier(new Modifier().style("align-items: center; padding: 8px 0;"));
    }
}
