package com.flux.plugin.example.pages.apps;

import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import io.jettra.flux.core.Modifier;
import io.jettra.flux.widgets.*;
import io.jettra.core.security.widget.PageWidgetAllow;
import java.util.Map;
import java.util.Arrays;
import com.flux.example.pages.template.TemplatePage;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER, jcf.AppRole.USER })
@io.jettra.core.server.Page(path = "/tasklist")
public class TasklistPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Task List - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget header = Row.of(
            Header.of(3, "Task Management").modifier(new Modifier().style("margin: 0;")),
            Button.of("Create Task").modifier(new Modifier().style("background: #3b82f6; color: white; border-radius: 6px; padding: 8px 16px;"))
        ).modifier(new Modifier().style("justify-content: space-between; align-items: center; margin-bottom: 30px; width: 100%;"));

        Widget columns = Row.of(
            buildTaskColumn("To Do", Arrays.asList(
                buildTaskCard("Design Homepage", "Create new wireframes", "High", "May 25"),
                buildTaskCard("Fix bugs", "Resolve UI issues on mobile", "Medium", "May 26")
            )),
            buildTaskColumn("In Progress", Arrays.asList(
                buildTaskCard("API Integration", "Connect to backend endpoints", "Critical", "May 24")
            )),
            buildTaskColumn("Completed", Arrays.asList(
                buildTaskCard("Setup Repository", "Initialize git and project structure", "Low", "May 20")
            ))
        ).modifier(new Modifier().style("display: flex; gap: 20px; width: 100%; align-items: flex-start;"));

        return Column.of(
            Header.of(2, "Task List App").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            Card.of(Column.of(header, columns)).modifier(new Modifier().style("width: 100%; padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-card);"))
        ).modifier(new Modifier().style("width: 100%; align-items: flex-start; max-width: 1200px; padding: 20px;"));
    }

    private Widget buildTaskColumn(String title, java.util.List<Widget> tasks) {
        Column col = Column.of(
            Header.of(5, title).modifier(new Modifier().style("margin-bottom: 15px; font-weight: 600; color: #1e293b; padding-bottom: 10px; border-bottom: 2px solid #e2e8f0;"))
        );
        for (Widget task : tasks) {
            col = (Column) col.add(task); // Assuming Column has an add method, or we can just pass them.
        }
        
        // Let's rebuild the Column correctly to avoid missing add method.
        Widget[] children = new Widget[tasks.size() + 1];
        children[0] = Header.of(5, title).modifier(new Modifier().style("margin-bottom: 15px; font-weight: 600; color: #1e293b; padding-bottom: 10px; border-bottom: 2px solid #e2e8f0;"));
        for (int i = 0; i < tasks.size(); i++) {
            children[i + 1] = tasks.get(i);
        }

        return Column.of(children)
            .modifier(new Modifier().style("flex: 1; background: #f8fafc; padding: 15px; border-radius: 8px; min-height: 400px; border: 1px solid #e2e8f0;"));
    }

    private Widget buildTaskCard(String title, String desc, String priority, String date) {
        String priorityColor = priority.equals("Critical") ? "#ef4444" : priority.equals("High") ? "#f97316" : priority.equals("Medium") ? "#eab308" : "#22c55e";
        
        return Card.of(Column.of(
            Row.of(
                Text.of(title).modifier(new Modifier().style("font-weight: 600; color: #1e293b;")),
                Icon.of(Icon.Heroicons.DOTS_VERTICAL).modifier(new Modifier().style("color: #94a3b8; width: 20px; cursor: pointer;"))
            ).modifier(new Modifier().style("justify-content: space-between; width: 100%; margin-bottom: 10px;")),
            Text.of(desc).modifier(new Modifier().style("font-size: 0.85rem; color: #64748b; margin-bottom: 15px;")),
            Row.of(
                Badge.of(priority).modifier(new Modifier().style("background: " + priorityColor + "20; color: " + priorityColor + "; border-radius: 12px; padding: 4px 8px; font-size: 0.75rem; font-weight: 600;")),
                Row.of(
                    Icon.of(Icon.Heroicons.CALENDAR).modifier(new Modifier().style("width: 14px; margin-right: 5px; color: #94a3b8;")),
                    Text.of(date).modifier(new Modifier().style("font-size: 0.75rem; color: #94a3b8;"))
                ).modifier(new Modifier().style("align-items: center;"))
            ).modifier(new Modifier().style("justify-content: space-between; width: 100%; align-items: center;"))
        )).modifier(new Modifier().style("padding: 15px; margin-bottom: 15px; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); border: 1px solid #e2e8f0; background: var(--surface-card); cursor: grab;"));
    }
}
