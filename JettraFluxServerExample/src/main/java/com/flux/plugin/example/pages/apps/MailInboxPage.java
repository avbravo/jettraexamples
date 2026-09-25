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
@io.jettra.core.server.Page(path = "/mail-inbox")
public class MailInboxPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Mail Inbox - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget sidebar = Column.of(
            Button.of("Compose New").modifier(new Modifier().style("margin-bottom: 20px; width: 100%;")),
            buildSidebarItem("Inbox", "12", true),
            buildSidebarItem("Starred", "", false),
            buildSidebarItem("Sent", "", false),
            buildSidebarItem("Drafts", "4", false),
            buildSidebarItem("Spam", "1", false),
            buildSidebarItem("Trash", "", false)
        ).modifier(new Modifier().style("width: 250px; padding: 20px; border-right: 1px solid #e2e8f0; background: #f8fafc;"));

        Widget toolbar = Row.of(
            Row.of(
                Checkbox.of(""),
                Button.of("Archive").modifier(new Modifier().style("margin-left: 15px;")),
                Button.of("Delete").modifier(new Modifier().style("margin-left: 10px;"))
            ).modifier(new Modifier().style("align-items: center;")),
            Row.of(
                TextBox.of("Search mail...").modifier(new Modifier().style("padding: 8px; border-radius: 6px; border: 1px solid #cbd5e1;"))
            ).modifier(new Modifier().style("align-items: center;"))
        ).modifier(new Modifier().style("justify-content: space-between; align-items: center; padding: 15px 20px; border-bottom: 1px solid #e2e8f0; width: 100%; background: var(--surface-card);"));

        Widget mailList = Column.of(
            buildMailItem("Sarah Jenkins", "Project Update for Q3", "Here is the latest project update for the third quarter...", "10:30 AM", true),
            buildMailItem("Amazon", "Your order has shipped!", "Your recent order containing 'Bamboo Watch' has been shipped.", "Yesterday", false),
            buildMailItem("GitHub", "[JettraStack] Pull Request #42", "John Doe has requested your review on a pull request.", "May 12", false),
            buildMailItem("LinkedIn", "You appeared in 5 searches this week", "See who is looking at your profile.", "May 10", false)
        ).modifier(new Modifier().style("flex: 1; overflow-y: auto; background: var(--surface-card);"));

        Widget mainArea = Column.of(
            toolbar,
            mailList
        ).modifier(new Modifier().style("flex: 1; display: flex; flex-direction: column;"));

        Widget mailApp = Row.of(sidebar, mainArea)
            .modifier(new Modifier().style("display: flex; height: 80vh; width: 100%; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);"));

        return Column.of(
            Header.of(2, "Mail Inbox").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            mailApp
        ).modifier(new Modifier().style("width: 100%; align-items: flex-start; max-width: 1200px; padding: 20px;"));
    }

    private Widget buildSidebarItem(String name, String badgeText, boolean active) {
        String bg = active ? "background: #e0f2fe; color: #0284c7; font-weight: 600; border-radius: 6px;" : "background: transparent; color: #475569;";
        Widget badge = badgeText.isEmpty() ? Text.of("") : Badge.of(badgeText).modifier(new Modifier().style("background: #ef4444; color: white; border-radius: 10px; padding: 2px 8px; font-size: 0.75rem;"));
        
        return Row.of(
            Text.of(name),
            badge
        ).modifier(new Modifier().style("justify-content: space-between; align-items: center; padding: 10px 15px; cursor: pointer; margin-bottom: 5px; " + bg));
    }

    private Widget buildMailItem(String sender, String subject, String snippet, String date, boolean unread) {
        String fontWeight = unread ? "font-weight: 700;" : "font-weight: 400;";
        String bgColor = unread ? "background: #f8fafc;" : "background: var(--surface-card);";
        
        return Row.of(
            Row.of(
                Checkbox.of("").modifier(new Modifier().style("margin-right: 15px;")),
                Icon.of(Icon.Heroicons.STAR).modifier(new Modifier().style("margin-right: 15px; color: #cbd5e1; width: 20px; height: 20px;")),
                Text.of(sender).modifier(new Modifier().style("width: 150px; " + fontWeight))
            ).modifier(new Modifier().style("align-items: center;")),
            Row.of(
                Text.of(subject).modifier(new Modifier().style(fontWeight + " margin-right: 10px; color: #1e293b;")),
                Text.of("- " + snippet).modifier(new Modifier().style("color: #64748b; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 400px;"))
            ).modifier(new Modifier().style("flex: 1; align-items: center; margin-right: 20px;")),
            Text.of(date).modifier(new Modifier().style("width: 80px; text-align: right; color: #64748b; " + fontWeight))
        ).modifier(new Modifier().style("padding: 15px 20px; border-bottom: 1px solid #e2e8f0; align-items: center; cursor: pointer; " + bgColor));
    }
}
