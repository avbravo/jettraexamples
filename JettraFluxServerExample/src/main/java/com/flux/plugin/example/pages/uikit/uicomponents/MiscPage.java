package com.flux.plugin.example.pages.uikit.uicomponents;

import io.jettra.flux.widgets.Icon;

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
@io.jettra.core.server.Page(path = "/misc")
public class MiscPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Misc - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        // --- ROW 1: ProgressBar ---
        Widget progressBarCard = Card.of(Column.of(
            Header.of(5, "ProgressBar").modifier(new Modifier().style("margin-top: 0; margin-bottom: 1.5rem; color: var(--text-color); font-weight: 600;")),
            ProgressBar.of().value(50).modifier(new Modifier().style("width: 100%; height: 20px; border-radius: 6px;"))
        )).modifier(new Modifier().style("margin-bottom: 20px; padding: 2rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);"));

        // --- ROW 2: Badges & Tags ---
        Widget badgesCard = Card.of(Column.of(
            Header.of(5, "Badge").modifier(new Modifier().style("margin-top: 0; margin-bottom: 1.5rem; color: var(--text-color); font-weight: 600;")),
            
            Header.of(6, "Numbers").modifier(new Modifier().style("margin-bottom: 1rem; color: var(--text-color); font-weight: 500;")),
            Row.of(
                Badge.of("2").severity("primary"),
                Badge.of("8").severity("success"),
                Badge.of("4").severity("info"),
                Badge.of("12").severity("warning"),
                Badge.of("3").severity("danger")
            ).modifier(new Modifier().style("gap: 1rem; flex-wrap: wrap; margin-bottom: 2rem;")),
            
            Header.of(6, "Positioned Badge").modifier(new Modifier().style("margin-bottom: 1rem; color: var(--text-color); font-weight: 500;")),
            Row.of(
                Avatar.icon(Icon.ENVELOPE).size("large").badge(Badge.of("10").severity("danger")),
                Avatar.icon(Icon.BELL).size("large").badge(Badge.of("4").severity("warning")),
                Avatar.icon(Icon.CALENDAR).size("large").badge(Badge.of("5").severity("success"))
            ).modifier(new Modifier().style("gap: 2rem; flex-wrap: wrap; align-items: center;"))
        )).modifier(new Modifier().style("flex: 1; padding: 2rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); margin-right: 10px;"));

        Widget tagsCard = Card.of(Column.of(
            Header.of(5, "Tag").modifier(new Modifier().style("margin-top: 0; margin-bottom: 1.5rem; color: var(--text-color); font-weight: 600;")),
            
            Header.of(6, "Tags").modifier(new Modifier().style("margin-bottom: 1rem; color: var(--text-color); font-weight: 500;")),
            Row.of(
                Tag.of("Primary").severity("primary"),
                Tag.of("Success").severity("success"),
                Tag.of("Info").severity("info"),
                Tag.of("Warning").severity("warning"),
                Tag.of("Danger").severity("danger")
            ).modifier(new Modifier().style("gap: 1rem; flex-wrap: wrap; margin-bottom: 2rem;")),
            
            Header.of(6, "Icons").modifier(new Modifier().style("margin-bottom: 1rem; color: var(--text-color); font-weight: 500;")),
            Row.of(
                Tag.of("Primary").severity("primary").icon("fas fa-user"),
                Tag.of("Success").severity("success").icon("fas fa-check"),
                Tag.of("Info").severity("info").icon("fas fa-info-circle"),
                Tag.of("Warning").severity("warning").icon("fas fa-exclamation-triangle"),
                Tag.of("Danger").severity("danger").icon("fas fa-times")
            ).modifier(new Modifier().style("gap: 1rem; flex-wrap: wrap; margin-bottom: 2rem;"))
        )).modifier(new Modifier().style("flex: 1; padding: 2rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); margin-left: 10px;"));

        Widget row2 = Row.of(badgesCard, tagsCard).modifier(new Modifier().style("display: flex; margin-bottom: 20px;"));

        // --- ROW 3: Avatar & Chips ---
        Widget avatarsCard = Card.of(Column.of(
            Header.of(5, "Avatar").modifier(new Modifier().style("margin-top: 0; margin-bottom: 1.5rem; color: var(--text-color); font-weight: 600;")),
            
            Header.of(6, "Avatar Group").modifier(new Modifier().style("margin-bottom: 1rem; color: var(--text-color); font-weight: 500;")),
            AvatarGroup.of(
                Avatar.image("https://primefaces.org/cdn/primevue/images/avatar/amyelsner.png").shape("circle").size("large"),
                Avatar.image("https://primefaces.org/cdn/primevue/images/avatar/asiyajavayant.png").shape("circle").size("large"),
                Avatar.image("https://primefaces.org/cdn/primevue/images/avatar/onyamalimba.png").shape("circle").size("large"),
                Avatar.image("https://primefaces.org/cdn/primevue/images/avatar/ionibowcher.png").shape("circle").size("large"),
                Avatar.label("+2").shape("circle").size("large")
            ).modifier(new Modifier().style("margin-bottom: 2rem;")),
            
            Header.of(6, "Label - Circle").modifier(new Modifier().style("margin-bottom: 1rem; color: var(--text-color); font-weight: 500;")),
            Row.of(
                Avatar.label("P").shape("circle").size("large"),
                Avatar.label("V").shape("circle").size("large").modifier(new Modifier().style("background-color: #22c55e; color: white;")),
                Avatar.label("U").shape("circle").size("large").modifier(new Modifier().style("background-color: #3b82f6; color: white;"))
            ).modifier(new Modifier().style("gap: 1rem; flex-wrap: wrap; align-items: center; margin-bottom: 2rem;")),
            
            Header.of(6, "Icon - Badge").modifier(new Modifier().style("margin-bottom: 1rem; color: var(--text-color); font-weight: 500;")),
            Row.of(
                Avatar.icon(Icon.USER).shape("circle").size("large").badge(Badge.of("4").severity("success"))
            ).modifier(new Modifier().style("gap: 1rem; flex-wrap: wrap; align-items: center;"))
        )).modifier(new Modifier().style("flex: 1; padding: 2rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); margin-right: 10px;"));

        Widget chipsCard = Card.of(Column.of(
            Header.of(5, "Chip").modifier(new Modifier().style("margin-top: 0; margin-bottom: 1.5rem; color: var(--text-color); font-weight: 600;")),
            
            Header.of(6, "Basic").modifier(new Modifier().style("margin-bottom: 1rem; color: var(--text-color); font-weight: 500;")),
            Row.of(
                Chip.of("Action"),
                Chip.of("Comedy"),
                Chip.of("Mystery"),
                Chip.of("Thriller").removable(true)
            ).modifier(new Modifier().style("gap: 1rem; flex-wrap: wrap; margin-bottom: 2rem;")),
            
            Header.of(6, "Icon").modifier(new Modifier().style("margin-bottom: 1rem; color: var(--text-color); font-weight: 500;")),
            Row.of(
                Chip.of("Settings").icon("fas fa-cog"),
                Chip.of("Contact").icon("fas fa-address-book"),
                Chip.of("Alert").icon("fas fa-exclamation-triangle"),
                Chip.of("Message").icon("fas fa-envelope").removable(true)
            ).modifier(new Modifier().style("gap: 1rem; flex-wrap: wrap; margin-bottom: 2rem;")),
            
            Header.of(6, "Image").modifier(new Modifier().style("margin-bottom: 1rem; color: var(--text-color); font-weight: 500;")),
            Row.of(
                Chip.of("Amy Elsner").image("https://primefaces.org/cdn/primevue/images/avatar/amyelsner.png"),
                Chip.of("Asiya Javayant").image("https://primefaces.org/cdn/primevue/images/avatar/asiyajavayant.png"),
                Chip.of("Onyama Limba").image("https://primefaces.org/cdn/primevue/images/avatar/onyamalimba.png")
            ).modifier(new Modifier().style("gap: 1rem; flex-wrap: wrap;"))
        )).modifier(new Modifier().style("flex: 1; padding: 2rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); margin-left: 10px;"));

        Widget row3 = Row.of(avatarsCard, chipsCard).modifier(new Modifier().style("display: flex; margin-bottom: 20px;"));

        // --- ROW 4: ScrollTop & Skeleton ---
        Widget scrollTopCard = Card.of(Column.of(
            Header.of(5, "ScrollTop").modifier(new Modifier().style("margin-top: 0; margin-bottom: 1.5rem; color: var(--text-color); font-weight: 600;")),
            Text.of("Scroll down the page to display the ScrollToTop button. This text simulates content so the page has enough height.").modifier(new Modifier().style("color: #64748b; margin-bottom: 15px; display: block; line-height: 1.5;")),
            Text.of("More content to ensure scrolling. Lorem ipsum dolor sit amet, consectetur adipiscing elit.").modifier(new Modifier().style("color: #64748b; margin-bottom: 15px; display: block; line-height: 1.5; height: 100px;")),
            Text.of("Even more content here. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.").modifier(new Modifier().style("color: #64748b; margin-bottom: 15px; display: block; line-height: 1.5; height: 100px;")),
            Text.of("And finally this text forces the scrollbar if screen is small enough. Ut enim ad minim veniam.").modifier(new Modifier().style("color: #64748b; display: block; line-height: 1.5; height: 100px;")),
            ScrollTop.of()
        )).modifier(new Modifier().style("flex: 1; padding: 2rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); margin-right: 10px;"));

        // Creating a custom mock-card layout for Skeleton to match PrimeNG closely
        Widget skeletonLayout = Div.of(
            Row.of(
                Skeleton.circle("4rem"),
                Column.of(
                    Skeleton.of("10rem", "1rem").modifier(new Modifier().style("margin-bottom: 0.5rem;")),
                    Skeleton.of("5rem", "1rem").modifier(new Modifier().style("margin-bottom: 0.5rem;")),
                    Skeleton.of("8rem", "0.5rem")
                ).modifier(new Modifier().style("margin-left: 1rem; justify-content: center; display: flex; flex-direction: column;"))
            ).modifier(new Modifier().style("align-items: center; margin-bottom: 1.5rem; display: flex;")),
            Skeleton.of("100%", "150px"),
            Row.of(
                Skeleton.of("4rem", "2rem"),
                Skeleton.of("4rem", "2rem")
            ).modifier(new Modifier().style("display: flex; justify-content: space-between; margin-top: 1.5rem;"))
        ).modifier(new Modifier().style("border: 1px solid rgba(128,128,128,0.2); border-radius: 12px; padding: 2rem;"));

        Widget skeletonCard = Card.of(Column.of(
            Header.of(5, "Skeleton").modifier(new Modifier().style("margin-top: 0; margin-bottom: 1.5rem; color: var(--text-color); font-weight: 600;")),
            skeletonLayout
        )).modifier(new Modifier().style("flex: 1; padding: 2rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); margin-left: 10px;"));

        Widget row4 = Row.of(scrollTopCard, skeletonCard).modifier(new Modifier().style("display: flex; margin-bottom: 20px;"));

        // --- ROW 5: MeterGroup ---
        Widget meterGroupCard = Card.of(Column.of(
            Header.of(5, "MeterGroup").modifier(new Modifier().style("margin-top: 0; margin-bottom: 1.5rem; color: var(--text-color); font-weight: 600;")),
            MeterGroup.of()
                .add("Space used", 15, "#3b82f6")
                .add("System", 20, "#eab308")
                .add("Media", 40, "#ec4899")
                .add("Apps", 10, "#8b5cf6")
        )).modifier(new Modifier().style("flex: 1; padding: 2rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); margin-right: 10px;"));

        Widget row5 = Row.of(meterGroupCard, Div.of().modifier(new Modifier().style("flex: 1; margin-left: 10px;"))).modifier(new Modifier().style("display: flex;"));

        return Column.of(
            Header.of(2, "Misc").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px; color: var(--text-color);")),
            progressBarCard,
            row2,
            row3,
            row4,
            row5
        ).modifier(new Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px;"));
    }
}
