package com.flux.plugin.example.pages.apps;

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
@io.jettra.core.server.Page(path = "/files")
public class FilesPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Files - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget sidebar = Column.of(
            Button.of("Upload New").modifier(new Modifier().style("margin-bottom: 20px; width: 100%;")),
            Header.of(5, "FOLDERS"),
            buildSidebarItem("Documents", Icon.Heroicons.DOCUMENT),
            buildSidebarItem("Images", Icon.Heroicons.PHOTOGRAPH),
            buildSidebarItem("Videos", Icon.Heroicons.FILM),
            buildSidebarItem("Work", Icon.Heroicons.BRIEFCASE),
            Divider.of(),
            Header.of(5, "STORAGE"),
            ProgressBar.of().modifier(new Modifier().style("margin-bottom: 10px; width: 100%;")),
            Text.of("75% used of 1TB").modifier(new Modifier().style("font-size: 0.85rem; color: #64748b;"))
        ).modifier(new Modifier().style("width: 250px; padding: 20px; border-right: 1px solid #e2e8f0; background: #f8fafc;"));

        Widget headerRow = Row.of(
            Header.of(3, "Recent Files").modifier(new Modifier().style("margin: 0;")),
            Row.of(
                TextBox.of("Search...").modifier(new Modifier().style("padding: 8px; border-radius: 6px; border: 1px solid #cbd5e1;")),
                Button.of("Search").modifier(new Modifier().style("margin-left: 10px;"))
            ).modifier(new Modifier().style("align-items: center;"))
        ).modifier(new Modifier().style("justify-content: space-between; align-items: center; margin-bottom: 20px; width: 100%;"));

        Widget filesGrid = Row.of(
            buildFileCard("Invoice_2023.pdf", "PDF", "2.5 MB", "12/05/2023"),
            buildFileCard("Project_Assets.zip", "Archive", "150 MB", "10/05/2023"),
            buildFileCard("Meeting_Notes.docx", "Word", "12 KB", "09/05/2023"),
            buildFileCard("Dashboard_Design.fig", "Figma", "12 MB", "08/05/2023"),
            buildFileCard("Vacation_Photo.jpg", "Image", "4.2 MB", "05/05/2023")
        ).modifier(new Modifier().style("display: flex; flex-wrap: wrap; gap: 20px;"));

        Widget mainArea = Column.of(
            headerRow,
            filesGrid
        ).modifier(new Modifier().style("flex: 1; padding: 30px; background: var(--surface-card);"));

        Widget fileApp = Row.of(sidebar, mainArea)
            .modifier(new Modifier().style("display: flex; min-height: 80vh; width: 100%; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);"));

        return Column.of(
            Header.of(2, "Files App").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            fileApp
        ).modifier(new Modifier().style("width: 100%; align-items: flex-start; max-width: 1200px; padding: 20px;"));
    }

    private Widget buildSidebarItem(String name, String iconType) {
        return Row.of(
            Icon.of(iconType).modifier(new Modifier().style("width: 20px; height: 20px; margin-right: 10px; color: #64748b;")),
            Text.of(name).modifier(new Modifier().style("font-weight: 500; color: #1e293b;"))
        ).modifier(new Modifier().style("padding: 10px 0; align-items: center; cursor: pointer;"));
    }

    private Widget buildFileCard(String name, String type, String size, String date) {
        return Card.of(Column.of(
            Icon.of(Icon.Heroicons.DOCUMENT).modifier(new Modifier().style("width: 48px; height: 48px; color: #3b82f6; margin-bottom: 15px;")),
            Text.of(name).modifier(new Modifier().style("font-weight: 600; margin-bottom: 5px; color: #1e293b; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 100%;")),
            Text.of(type + " - " + size).modifier(new Modifier().style("font-size: 0.85rem; color: #64748b; margin-bottom: 10px;")),
            Divider.of(),
            Text.of("Modified: " + date).modifier(new Modifier().style("font-size: 0.75rem; color: #94a3b8; margin-top: 10px;"))
        )).modifier(new Modifier().style("width: 200px; padding: 20px; border-radius: 12px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); border: 1px solid #f1f5f9; display: flex; flex-direction: column; align-items: center; text-align: center; cursor: pointer; transition: transform 0.2s;"));
    }
}
