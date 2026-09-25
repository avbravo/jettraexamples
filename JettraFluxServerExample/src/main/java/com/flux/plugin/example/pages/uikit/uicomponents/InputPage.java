    package com.flux.plugin.example.pages.uikit.uicomponents;

import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Row;
import io.jettra.flux.widgets.Card;
import io.jettra.flux.widgets.Header;
import io.jettra.flux.widgets.TextField;
import io.jettra.flux.widgets.TextArea;
import io.jettra.flux.widgets.Checkbox;
import io.jettra.flux.widgets.RadioButton;
import io.jettra.flux.widgets.ToggleSwitch;
import io.jettra.flux.widgets.Label;
import io.jettra.core.security.widget.PageWidgetAllow;
import java.util.Map;
import com.flux.example.pages.template.TemplatePage;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER, jcf.AppRole.USER })
@io.jettra.core.server.Page(path = "/input")
public class InputPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Input Components - JettraFlux Pro";
    }

    private Widget createInputGroup(String title, Widget input) {
        Column col = Column.of(Header.of(5, title).modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 1rem; color: #334155; font-weight: 600;")), input);
        return Card.of(col).modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 2rem; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);"));
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        // 1. Text Inputs
        Widget textInputs = createInputGroup("Input Text",
            Row.of(
                Column.of(
                    Label.of("Basic").forId("name"),
                    TextField.of("name", "Name").modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0.5rem; margin-bottom: 1rem; width: 250px;"))
                ),
                Column.of(
                    Label.of("Email").forId("email"),
                    TextField.of("email", "Email address").modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0.5rem; margin-bottom: 1rem; width: 250px;"))
                ).modifier(new io.jettra.flux.core.Modifier().style("margin-left: 2rem;"))
            )
        );
        Widget textInputs2 = createInputGroup("Input Text",
            Row.of(
                Column.of(
                    Label.of("Basic").forId("name"),
                    TextField.of("name", "Name")
                ),
                Column.of(
                    Label.of("Email").forId("email"),
                    TextField.of("email", "Email address")
                ).modifier(new io.jettra.flux.core.Modifier().style("margin-left: 2rem;"))
            )
        );

        // 2. Text Area
        Widget textArea = createInputGroup("Text Area",
            Column.of(
                Label.of("Description").forId("desc"),
                TextArea.create().name("desc").placeholder("Enter your description here...").rows(5).cols(40).modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0.5rem; width: 100%; max-width: 500px; padding: 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-family: inherit;"))
            )
        );

        // 3. Checkboxes
        Widget checkboxes = createInputGroup("Checkboxes",
            Row.of(
                Row.of(Checkbox.of("cb1", "Option 1"), Label.of("Option 1").forId("cb1").modifier(new io.jettra.flux.core.Modifier().style("margin-left: 0.5rem;"))).modifier(new io.jettra.flux.core.Modifier().style("align-items: center; margin-right: 2rem;")),
                Row.of(Checkbox.of("cb2", "Option 2"), Label.of("Option 2").forId("cb2").modifier(new io.jettra.flux.core.Modifier().style("margin-left: 0.5rem;"))).modifier(new io.jettra.flux.core.Modifier().style("align-items: center;"))
            )
        );

        // 4. Radio Buttons
        Widget radioButtons = createInputGroup("Radio Buttons",
            Row.of(
                Row.of(RadioButton.create().name("group1").value("val1"), Label.of("Choice A").forId("val1").modifier(new io.jettra.flux.core.Modifier().style("margin-left: 0.5rem;"))).modifier(new io.jettra.flux.core.Modifier().style("align-items: center; margin-right: 2rem;")),
                Row.of(RadioButton.create().name("group1").value("val2"), Label.of("Choice B").forId("val2").modifier(new io.jettra.flux.core.Modifier().style("margin-left: 0.5rem;"))).modifier(new io.jettra.flux.core.Modifier().style("align-items: center;"))
            )
        );

        // 5. Toggle Switch
        Widget toggleSwitch = createInputGroup("Toggle Switch",
            Row.of(
                ToggleSwitch.of().modifier(new io.jettra.flux.core.Modifier().style("margin-right: 1rem;")),
                Label.of("Activate Feature").forId("ts1")
            ).modifier(new io.jettra.flux.core.Modifier().style("align-items: center;"))
        );

        return Column.of(
            Header.of(2, "Input Components").modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            textInputs,
            textInputs2,
            textArea,
            checkboxes,
            radioButtons,
            toggleSwitch
        ).modifier(new io.jettra.flux.core.Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px;"));
    }
}
