package com.flux.plugin.example.pages.uikit.uicomponents;

import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Header;
import io.jettra.flux.widgets.Paragraph;
import io.jettra.flux.widgets.Panel;
import io.jettra.flux.widgets.Accordion;
import io.jettra.flux.widgets.AccordionTab;
import io.jettra.flux.widgets.TabView;
import io.jettra.flux.widgets.TabPanel;
import io.jettra.flux.widgets.Divider;
import io.jettra.core.security.widget.PageWidgetAllow;
import java.util.Map;
import com.flux.example.pages.template.TemplatePage;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER, jcf.AppRole.USER })
@io.jettra.core.server.Page(path = "/panel")
public class PanelPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Panels - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget panelComponent = Panel.of("Panel Header", 
            Paragraph.of("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
        ).toggleable(true);

        Widget dividerComponent = Column.of(
            Paragraph.of("Content above divider"),
            Divider.of(),
            Paragraph.of("Content below divider"),
            Divider.of("OR"),
            Paragraph.of("Content below text divider")
        );
        
        Widget panelWithDivider = Panel.of("Divider Example", dividerComponent);

        Widget accordionComponent = Accordion.of(
            AccordionTab.of("Header I", Paragraph.of("Content I - Lorem ipsum dolor sit amet.")).active(true),
            AccordionTab.of("Header II", Paragraph.of("Content II - Sed do eiusmod tempor incididunt.")),
            AccordionTab.of("Header III", Paragraph.of("Content III - Ut enim ad minim veniam."))
        );
        
        Widget accordionPanel = Panel.of("Accordion", accordionComponent);

        Widget tabViewComponent = TabView.of(
            TabPanel.of("Header I", Paragraph.of("Tab Content I")).active(true),
            TabPanel.of("Header II", Paragraph.of("Tab Content II")),
            TabPanel.of("Header III", Paragraph.of("Tab Content III"))
        );
        
        Widget tabViewPanel = Panel.of("TabView", tabViewComponent);

        return Column.of(
            Paragraph.of(io.jettra.flux.theme.PanelCSS.get() + io.jettra.flux.theme.AccordionCSS.get() + io.jettra.flux.theme.TabViewCSS.get() + io.jettra.flux.theme.DividerCSS.get()),
            Header.of(2, "Panel Components").modifier(new io.jettra.flux.core.Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px;")),
            panelComponent,
            panelWithDivider,
            accordionPanel,
            tabViewPanel
        ).modifier(new io.jettra.flux.core.Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px; gap: 1rem;"));
    }
}
