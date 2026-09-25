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
@io.jettra.core.server.Page(path = "/chat")
public class ChatPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Chat - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget userList = Column.of(
            Header.of(4, "Messages").modifier(new Modifier().style("margin-bottom: 20px;")),
            buildUserItem("John Doe", "Hey! How are you?", "10:30 AM", true),
            buildUserItem("Jane Smith", "I will send the files later.", "09:15 AM", false),
            buildUserItem("Mike Johnson", "Thanks!", "Yesterday", false)
        ).modifier(new Modifier().style("width: 300px; padding: 20px; border-right: 1px solid #e2e8f0; background: #f8fafc; overflow-y: auto;"));

        Widget chatHeader = Row.of(
            Avatar.of("https://primefaces.org/cdn/primeng/images/avatar/amyelsner.png").shape("circle"),
            Column.of(
                Text.of("John Doe").modifier(new Modifier().style("font-weight: bold; font-size: 1.1rem;")),
                Text.of("Online").modifier(new Modifier().style("color: #22c55e; font-size: 0.85rem;"))
            ).modifier(new Modifier().style("margin-left: 10px;"))
        ).modifier(new Modifier().style("padding: 20px; border-bottom: 1px solid #e2e8f0; align-items: center; background: var(--surface-card);"));

        Widget chatMessages = Column.of(
            buildMessage("Hi, how are you doing?", "10:25 AM", false),
            buildMessage("I'm doing great! Just working on the new JettraFlux project.", "10:28 AM", true),
            buildMessage("Hey! How are you?", "10:30 AM", false)
        ).modifier(new Modifier().style("flex: 1; padding: 20px; overflow-y: auto; background: #f1f5f9; gap: 15px;"));

        Widget chatInput = Row.of(
            TextBox.of("Type a message...").modifier(new Modifier().style("flex: 1; padding: 10px; border-radius: 20px; border: 1px solid #cbd5e1; outline: none; margin-right: 10px;")),
            Button.of("Send").modifier(new Modifier().style("border-radius: 20px; padding: 10px 20px;"))
        ).modifier(new Modifier().style("padding: 20px; border-top: 1px solid #e2e8f0; background: var(--surface-card); align-items: center;"));

        Widget chatArea = Column.of(
            chatHeader,
            chatMessages,
            chatInput
        ).modifier(new Modifier().style("flex: 1; display: flex; flex-direction: column;"));

        Widget chatApp = Row.of(userList, chatArea)
            .modifier(new Modifier().style("display: flex; height: 80vh; width: 100%; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);"));

        return Column.of(
            Header.of(2, "Chat App").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            chatApp
        ).modifier(new Modifier().style("width: 100%; align-items: flex-start; max-width: 1200px; padding: 20px;"));
    }

    private Widget buildUserItem(String name, String lastMsg, String time, boolean active) {
        String bg = active ? "background: #e0f2fe;" : "background: transparent;";
        return Row.of(
            Avatar.of("https://primefaces.org/cdn/primeng/images/avatar/amyelsner.png").shape("circle"),
            Column.of(
                Row.of(
                    Text.of(name).modifier(new Modifier().style("font-weight: 600;")),
                    Text.of(time).modifier(new Modifier().style("font-size: 0.75rem; color: #64748b;"))
                ).modifier(new Modifier().style("justify-content: space-between; width: 100%;")),
                Text.of(lastMsg).modifier(new Modifier().style("font-size: 0.85rem; color: #64748b; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"))
            ).modifier(new Modifier().style("margin-left: 10px; flex: 1;"))
        ).modifier(new Modifier().style("padding: 10px; border-radius: 8px; margin-bottom: 10px; cursor: pointer; align-items: center; " + bg));
    }

    private Widget buildMessage(String msg, String time, boolean isSelf) {
        String align = isSelf ? "align-self: flex-end; background: #3b82f6; color: white;" : "align-self: flex-start; background: var(--surface-card); color: #1e293b;";
        return Column.of(
            Text.of(msg).modifier(new Modifier().style("padding: 12px 16px; border-radius: 16px; box-shadow: 0 1px 2px rgba(0,0,0,0.05); " + align)),
            Text.of(time).modifier(new Modifier().style("font-size: 0.75rem; color: #94a3b8; margin-top: 5px; " + (isSelf ? "align-self: flex-end;" : "")))
        ).modifier(new Modifier().style("max-width: 70%; display: flex; flex-direction: column;"));
    }
}
