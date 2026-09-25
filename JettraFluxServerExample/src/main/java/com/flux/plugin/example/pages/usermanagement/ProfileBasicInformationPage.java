package com.flux.plugin.example.pages.usermanagement;

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
@io.jettra.core.server.Page(path = "/profile-basic-information")
public class ProfileBasicInformationPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Create Profile - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget sidebar = Column.of(
            Text.of("Basic Information").modifier(new Modifier().style("font-weight: 600; color: var(--primary-color); padding: 10px; background: rgba(59,130,246,0.1); border-radius: 6px; margin-bottom: 5px;")),
            Text.of("Business Information").modifier(new Modifier().style("font-weight: 500; color: var(--text-color-secondary); padding: 10px; cursor: pointer;")),
            Text.of("Location Information").modifier(new Modifier().style("font-weight: 500; color: var(--text-color-secondary); padding: 10px; cursor: pointer;")),
            Text.of("Authorization and Access").modifier(new Modifier().style("font-weight: 500; color: var(--text-color-secondary); padding: 10px; cursor: pointer;")),
            Text.of("Account Status").modifier(new Modifier().style("font-weight: 500; color: var(--text-color-secondary); padding: 10px; cursor: pointer;"))
        ).modifier(new Modifier().style("width: 250px; padding: 20px; border-right: 1px solid var(--surface-alt); background: var(--background-color); min-height: 500px;"));

        Widget avatarSection = Row.of(
            Avatar.of("https://primefaces.org/cdn/primeng/images/avatar/amyelsner.png").shape("circle").modifier(new Modifier().style("width: 80px; height: 80px; margin-right: 20px;")),
            Column.of(
                Button.of("Upload New Photo").modifier(new Modifier().style("background: var(--surface-color); color: var(--text-color); border: 1px solid var(--surface-alt); padding: 8px 16px; border-radius: 6px; font-weight: 600; margin-bottom: 10px; width: max-content;")),
                Text.of("Allowed JPG, GIF or PNG. Max size of 800K").modifier(new Modifier().style("font-size: 0.85rem; color: var(--text-color-secondary);"))
            ).modifier(new Modifier().style("justify-content: center;"))
        ).modifier(new Modifier().style("align-items: center; margin-bottom: 30px;"));

        Widget form = Column.of(
            Row.of(
                Column.of(Label.of("First Name").modifier(new Modifier().style("margin-bottom: 5px; font-weight: 600;")), TextField.of("fname", "Amy").modifier(new Modifier().style("padding: 10px; border-radius: 6px; border: 1px solid var(--surface-alt); background: var(--surface-color); color: var(--text-color); width: 100%;"))).modifier(new Modifier().style("flex: 1; margin-right: 20px;")),
                Column.of(Label.of("Last Name").modifier(new Modifier().style("margin-bottom: 5px; font-weight: 600;")), TextField.of("lname", "Elsner").modifier(new Modifier().style("padding: 10px; border-radius: 6px; border: 1px solid var(--surface-alt); background: var(--surface-color); color: var(--text-color); width: 100%;"))).modifier(new Modifier().style("flex: 1;"))
            ).modifier(new Modifier().style("width: 100%; margin-bottom: 20px;")),
            
            Row.of(
                Column.of(Label.of("Email").modifier(new Modifier().style("margin-bottom: 5px; font-weight: 600;")), TextField.of("email", "amy@example.com").modifier(new Modifier().style("padding: 10px; border-radius: 6px; border: 1px solid var(--surface-alt); background: var(--surface-color); color: var(--text-color); width: 100%;"))).modifier(new Modifier().style("flex: 1; margin-right: 20px;")),
                Column.of(Label.of("Phone").modifier(new Modifier().style("margin-bottom: 5px; font-weight: 600;")), TextField.of("phone", "+1 123 456 7890").modifier(new Modifier().style("padding: 10px; border-radius: 6px; border: 1px solid var(--surface-alt); background: var(--surface-color); color: var(--text-color); width: 100%;"))).modifier(new Modifier().style("flex: 1;"))
            ).modifier(new Modifier().style("width: 100%; margin-bottom: 20px;")),

            Column.of(
                Label.of("Bio").modifier(new Modifier().style("margin-bottom: 5px; font-weight: 600;")),
                TextArea.create().name("bio").rows(4).placeholder("A short bio about yourself...").modifier(new Modifier().style("padding: 10px; border-radius: 6px; border: 1px solid var(--surface-alt); background: var(--surface-color); color: var(--text-color); width: 100%; font-family: inherit;"))
            ).modifier(new Modifier().style("width: 100%; margin-bottom: 30px;")),

            Row.of(
                Button.of("Cancel").modifier(new Modifier().style("background: var(--surface-color); color: var(--text-color); border: 1px solid var(--surface-alt); padding: 10px 20px; border-radius: 6px; font-weight: 600; margin-right: 15px;")),
                Button.of("Save Changes").modifier(new Modifier().style("background: var(--primary-color); color: var(--on-primary-color); border:none; padding: 10px 20px; border-radius: 6px; font-weight: 600;"))
            ).modifier(new Modifier().style("justify-content: flex-end; width: 100%;"))
        ).modifier(new Modifier().style("width: 100%;"));

        Widget mainArea = Column.of(
            Header.of(4, "Basic Information").modifier(new Modifier().style("margin-bottom: 30px; font-weight: 700; color: var(--text-color);")),
            avatarSection,
            form
        ).modifier(new Modifier().style("flex: 1; padding: 40px; background: var(--surface-color);"));

        Widget mainCard = Card.of(Row.of(sidebar, mainArea).modifier(new Modifier().style("width: 100%; align-items: stretch;")))
            .modifier(new Modifier().style("width: 100%; padding: 0; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); overflow: hidden;"));

        return Column.of(
            Header.of(2, "Create Profile").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px; color: var(--text-color);")),
            mainCard
        ).modifier(new Modifier().style("width: 100%; align-items: stretch; max-width: 1000px; padding: 20px; margin: 0 auto;"));
    }
}
