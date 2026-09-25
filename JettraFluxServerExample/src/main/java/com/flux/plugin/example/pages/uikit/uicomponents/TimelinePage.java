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
@io.jettra.core.server.Page(path = "/timeline")
public class TimelinePage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Timeline - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget leftAlignTimeline = Column.of(
            Header.of(5, "Left Align").modifier(new Modifier().style("margin-bottom: 2rem; color: var(--text-color); font-weight: 600;")),
            TimeLine.of(
                TimeLineItem.of()
                    .opposite(Text.of("15/10/2020 10:30").modifier(new Modifier().style("color: var(--text-color-secondary); font-size: 0.875rem;")))
                    .markerIcon(Icon.ENVELOPE)
                    .markerColor("#3b82f6")
                    .content(Card.of(Column.of(
                        Header.of(6, "Message Sent").modifier(new Modifier().style("margin: 0; font-weight: 600; color: var(--text-color);")),
                        Text.of("and fully received by the user").modifier(new Modifier().style("margin-top: 0.5rem; color: var(--text-color-secondary);"))
                    )).modifier(new Modifier().style("padding: 1.5rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); width: 100%;"))),
                
                TimeLineItem.of()
                    .opposite(Text.of("15/10/2020 14:00").modifier(new Modifier().style("color: var(--text-color-secondary); font-size: 0.875rem;")))
                    .markerIcon(Icon.CHECK)
                    .markerColor("#22c55e")
                    .content(Card.of(Column.of(
                        Header.of(6, "Order Processing").modifier(new Modifier().style("margin: 0; font-weight: 600; color: var(--text-color);")),
                        Text.of("Processing the payment and order").modifier(new Modifier().style("margin-top: 0.5rem; color: var(--text-color-secondary);"))
                    )).modifier(new Modifier().style("padding: 1.5rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); width: 100%;"))),
                
                TimeLineItem.of()
                    .opposite(Text.of("15/10/2020 16:15").modifier(new Modifier().style("color: var(--text-color-secondary); font-size: 0.875rem;")))
                    .markerIcon(Icon.TRUCK)
                    .markerColor("#f59e0b")
                    .content(Card.of(Column.of(
                        Header.of(6, "Shipped").modifier(new Modifier().style("margin: 0; font-weight: 600; color: var(--text-color);")),
                        Text.of("Your order is on the way").modifier(new Modifier().style("margin-top: 0.5rem; color: var(--text-color-secondary);"))
                    )).modifier(new Modifier().style("padding: 1.5rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); width: 100%;"))),
                
                TimeLineItem.of()
                    .opposite(Text.of("16/10/2020 10:00").modifier(new Modifier().style("color: var(--text-color-secondary); font-size: 0.875rem;")))
                    .markerIcon(Icon.BOX)
                    .markerColor("#8b5cf6")
                    .content(Card.of(Column.of(
                        Header.of(6, "Delivered").modifier(new Modifier().style("margin: 0; font-weight: 600; color: var(--text-color);")),
                        Text.of("The package has been delivered").modifier(new Modifier().style("margin-top: 0.5rem; color: var(--text-color-secondary);"))
                    )).modifier(new Modifier().style("padding: 1.5rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); width: 100%;")))
            )
        ).modifier(new Modifier().style("flex: 1; padding: 2rem; background: transparent; margin-bottom: 20px;"));
        
        Widget alternateTimeline = Column.of(
            Header.of(5, "Alternate Align").modifier(new Modifier().style("margin-bottom: 2rem; color: var(--text-color); font-weight: 600;")),
            TimeLine.of(
                TimeLineItem.of()
                    .opposite(Card.of(Column.of(
                        Header.of(6, "System Error").modifier(new Modifier().style("margin: 0; font-weight: 600; color: var(--text-color);")),
                        Text.of("Database connection failed").modifier(new Modifier().style("margin-top: 0.5rem; color: var(--text-color-secondary);"))
                    )).modifier(new Modifier().style("padding: 1.5rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); width: 100%;")))
                    .markerIcon(Icon.TIMES)
                    .markerColor("#ef4444")
                    .content(Text.of("10/05/2022 10:30").modifier(new Modifier().style("color: var(--text-color-secondary); font-size: 0.875rem;"))),
                
                TimeLineItem.of()
                    .opposite(Text.of("10/05/2022 11:15").modifier(new Modifier().style("color: var(--text-color-secondary); font-size: 0.875rem;")))
                    .markerIcon(Icon.WRENCH)
                    .markerColor("#3b82f6")
                    .content(Card.of(Column.of(
                        Header.of(6, "Maintenance Started").modifier(new Modifier().style("margin: 0; font-weight: 600; color: var(--text-color);")),
                        Text.of("Engineers are working on the issue").modifier(new Modifier().style("margin-top: 0.5rem; color: var(--text-color-secondary);"))
                    )).modifier(new Modifier().style("padding: 1.5rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); width: 100%;"))),
                
                TimeLineItem.of()
                    .opposite(Card.of(Column.of(
                        Header.of(6, "System Restored").modifier(new Modifier().style("margin: 0; font-weight: 600; color: var(--text-color);")),
                        Text.of("All services operational").modifier(new Modifier().style("margin-top: 0.5rem; color: var(--text-color-secondary);"))
                    )).modifier(new Modifier().style("padding: 1.5rem; background: var(--surface-color); border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); width: 100%;")))
                    .markerIcon(Icon.CHECK)
                    .markerColor("#22c55e")
                    .content(Text.of("10/05/2022 12:45").modifier(new Modifier().style("color: var(--text-color-secondary); font-size: 0.875rem;")))
            )
        ).modifier(new Modifier().style("flex: 1; padding: 2rem; background: transparent; margin-bottom: 20px;"));
        
        Widget timelinesContainer = Row.of(leftAlignTimeline, alternateTimeline)
            .modifier(new Modifier().style("display: flex; flex-wrap: wrap; gap: 2rem; width: 100%; align-items: flex-start;"));

        Widget horizontalTimeline = Column.of(
            Header.of(5, "Horizontal").modifier(new Modifier().style("margin-bottom: 2rem; color: var(--text-color); font-weight: 600;")),
            TimeLine.of(
                TimeLineItem.of()
                    .opposite(Text.of("15/10/2020 10:30").modifier(new Modifier().style("color: var(--text-color-secondary); font-size: 0.875rem;")))
                    .markerIcon(Icon.ENVELOPE)
                    .markerColor("#3b82f6")
                    .content(Header.of(6, "Message Sent").modifier(new Modifier().style("margin: 0; font-weight: 600; color: var(--text-color);"))),
                
                TimeLineItem.of()
                    .opposite(Text.of("15/10/2020 14:00").modifier(new Modifier().style("color: var(--text-color-secondary); font-size: 0.875rem;")))
                    .markerIcon(Icon.CHECK)
                    .markerColor("#22c55e")
                    .content(Header.of(6, "Order Processing").modifier(new Modifier().style("margin: 0; font-weight: 600; color: var(--text-color);"))),
                
                TimeLineItem.of()
                    .opposite(Text.of("15/10/2020 16:15").modifier(new Modifier().style("color: var(--text-color-secondary); font-size: 0.875rem;")))
                    .markerIcon(Icon.TRUCK)
                    .markerColor("#f59e0b")
                    .content(Header.of(6, "Shipped").modifier(new Modifier().style("margin: 0; font-weight: 600; color: var(--text-color);"))),
                
                TimeLineItem.of()
                    .opposite(Text.of("16/10/2020 10:00").modifier(new Modifier().style("color: var(--text-color-secondary); font-size: 0.875rem;")))
                    .markerIcon(Icon.BOX)
                    .markerColor("#8b5cf6")
                    .content(Header.of(6, "Delivered").modifier(new Modifier().style("margin: 0; font-weight: 600; color: var(--text-color);")))
            ).layout("horizontal")
        ).modifier(new Modifier().style("flex: 1; padding: 2rem; background: transparent; margin-bottom: 20px; width: 100%;"));

        return Column.of(
            Header.of(2, "Timeline").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px; color: var(--text-color);")),
            timelinesContainer,
            horizontalTimeline
        ).modifier(new Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px;"));
    }
}
