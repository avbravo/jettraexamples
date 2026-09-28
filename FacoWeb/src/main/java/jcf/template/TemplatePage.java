package jcf.template;

import io.jettra.flux.pages.FluxBaseHandler;
import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;

import io.jettra.flux.widgets.ActionIcon;
import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Dashboard;
import io.jettra.flux.widgets.Footer;
import io.jettra.flux.widgets.Header;
import io.jettra.flux.widgets.Icon;
import io.jettra.flux.widgets.Left;
import io.jettra.flux.widgets.Paragraph;
import io.jettra.flux.widgets.Row;
import io.jettra.flux.widgets.Scaffold;
import io.jettra.flux.widgets.SidebarCategory;
import io.jettra.flux.widgets.SidebarLogo;
import io.jettra.flux.widgets.ThemeChanged;
import io.jettra.flux.widgets.Top;
import io.jettra.flux.widgets.WidgetLet;
import io.jettra.flux.widgets.NotificationTop;
import io.jettra.server.JettraServer;
import java.util.Map;

public abstract class TemplatePage extends FluxBaseHandler {

    protected abstract Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme);

    public static NotificationTop getNotificationTop(String idnotification) {
        io.jettra.server.core.JettraContext ctx = io.jettra.server.core.JettraContext.getCurrent();
        if (ctx == null) return null;
        
        Map<String, NotificationTop> notifications = 
            (Map<String, NotificationTop>) ctx.get(io.jettra.server.core.JettraContext.Scope.SESSION, "template_notifications");
            
        if (notifications == null) {
            notifications = new java.util.HashMap<>();
            ctx.set(io.jettra.server.core.JettraContext.Scope.SESSION, "template_notifications", notifications);
        }
        
        NotificationTop nt = notifications.get(idnotification);
        if (nt == null) {
            nt = NotificationTop.of().binding(idnotification);
            notifications.put(idnotification, nt);
        }
        return nt;
    }

    @Override
    protected Widget buildUI(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        String username = getLoggedUser(exchange);
        if (username == null || username.isEmpty()) {
            try {
                redirect(exchange, "/login");
            } catch (Exception e) {
            }
            return Column.of();
        }

        String userInitial = username.substring(0, 1).toUpperCase();

        io.jettra.flux.model.CredentialFlux credential = (io.jettra.flux.model.CredentialFlux) io.jettra.server.core.JettraContext.getCurrent().get(io.jettra.server.core.JettraContext.Scope.SESSION, "credentialFlux");
        String displayName = username;
        
        Widget photoWidget = io.jettra.flux.widgets.Avatar.label(userInitial).shape("circle")
            .modifier(new io.jettra.flux.core.Modifier().style("background-color:#3b82f6; color:white; font-weight:bold; margin-right:8px;"));

        if (credential != null) {
            displayName = credential.name() != null && !credential.name().isEmpty() ? credential.name() : username;
            if (credential.photo() != null && !credential.photo().isEmpty()) {
                photoWidget = io.jettra.flux.widgets.Avatar.image(credential.photo()).shape("circle")
                    .modifier(new io.jettra.flux.core.Modifier().style("margin-right:8px;"));
            } else {
                photoWidget = io.jettra.flux.widgets.Avatar.icon("fas fa-user").shape("circle")
                    .modifier(new io.jettra.flux.core.Modifier().style("background-color:#3b82f6; color:white; font-weight:bold; margin-right:8px;"));
            }
        }

        Widget customCss = Paragraph.of(io.jettra.flux.theme.OceanTheme.Template.CustomCSS + "\n" + io.jettra.flux.theme.OceanTheme.Template.CustomJS);

        WidgetLet mainCategoryMenu = WidgetLet.of("Navigation").icon(Icon.HOME);
        mainCategoryMenu.add(WidgetLet.of("Dashboard").icon(Icon.CHART_LINE).url(JettraServer.resolvePath("/dashboard")));
        mainCategoryMenu.add(WidgetLet.of("Person").icon(Icon.USER).url(JettraServer.resolvePath("/person")));

        Widget menu = Left.of(
                SidebarLogo.of(Icon.LAYER_GROUP, "Ocean"),
                SidebarCategory.of("Navigation"),
                mainCategoryMenu
        ).modifier(new io.jettra.flux.core.Modifier().cssClass("professional-left"));

        Widget profileTrigger = Row.of(
            photoWidget,
            io.jettra.flux.widgets.Span.of("").modifier(new io.jettra.flux.core.Modifier().style("font-weight:bold;")),
            Icon.of("fas fa-caret-down").modifier(new io.jettra.flux.core.Modifier().style("margin-left:5px;"))
        ).modifier(new io.jettra.flux.core.Modifier().style("align-items:center; cursor:pointer;").attribute("title", displayName));

        Widget profileMenu = ((io.jettra.flux.widgets.OverlayMenu) io.jettra.flux.widgets.OverlayMenu.of(
                WidgetLet.of("Logout").icon(Icon.SIGN_OUT_ALT).url(JettraServer.resolvePath("/login?logout=true"))
        ).trigger(profileTrigger)).alignRight();

        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        String currentLang = "en";
        if (cookieHeader != null && cookieHeader.contains("jettra_lang=es")) {
            currentLang = "es";
        }

        Widget langTrigger;
        io.jettra.flux.widgets.WidgetLet langOption;
        if ("es".equals(currentLang)) {
            langTrigger = io.jettra.flux.widgets.Span.of("🇪🇸").modifier(new io.jettra.flux.core.Modifier().attribute("title", "Español").style("cursor:pointer; font-size:1.2rem;"));
            langOption = (io.jettra.flux.widgets.WidgetLet) io.jettra.flux.widgets.WidgetLet.of("🇺🇸").url("?change_lang=en");
        } else {
            langTrigger = io.jettra.flux.widgets.Span.of("🇺🇸").modifier(new io.jettra.flux.core.Modifier().attribute("title", "English").style("cursor:pointer; font-size:1.2rem;"));
            langOption = (io.jettra.flux.widgets.WidgetLet) io.jettra.flux.widgets.WidgetLet.of("🇪🇸").url("?change_lang=es");
        }

        Widget langSwitcher = ((io.jettra.flux.widgets.OverlayMenu) io.jettra.flux.widgets.OverlayMenu.of(langOption).trigger(langTrigger)).alignRight();

        NotificationTop globalNotif = getNotificationTop("global_notif").type(NotificationTop.NotificationTopType.GLOBAL).icon(Icon.of("fas fa-globe"));
        NotificationTop personalNotif = getNotificationTop("personal_notif").type(NotificationTop.NotificationTopType.PERSONAL).icon(Icon.of("fas fa-envelope"));
        NotificationTop channelNotif = getNotificationTop("channel_notif").type(NotificationTop.NotificationTopType.CHANNEL).channel("admin_channel").icon(Icon.of("fas fa-bullhorn"));

        Widget topBar = Top.of(
                Row.of(
                        ActionIcon.of(Icon.BARS + " top-bars-icon", "toggleSidebar()"),
                        Header.of(4, "Dashboard").modifier(new io.jettra.flux.core.Modifier().cssClass("top-dashboard-title"))
                ).modifier(new io.jettra.flux.core.Modifier().cssClass("top-left-section")),
                Row.of(
                        Icon.of(Icon.SEARCH),
                        globalNotif,
                        personalNotif,
                        channelNotif,
                        langSwitcher,
                        ThemeChanged.of().current(currentTheme),
                        profileMenu
                ).modifier(new io.jettra.flux.core.Modifier().cssClass("top-right-section").style("gap: 15px; align-items: center;"))
        );

        Widget centerContent = Column.of(
                customCss,
                buildCenter(exchange, params, currentTheme)
        ).modifier(new io.jettra.flux.core.Modifier().cssClass("professional-center espresso-center"));

        Widget footerContent = Footer.of(
                Paragraph.of("© 2026 JettraStack - JettraFlux")
        );

        Widget body = Dashboard.of(
                topBar,
                menu,
                centerContent,
                footerContent
        );

        return Scaffold.of().body(body);
    }
}
