package com.flux.plugin.example.pages.example;

import io.jettra.core.security.widget.PageWidgetAllow;
import io.jettra.core.server.Page;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER })
@Page(path = "/rules")
public class RulesPage extends ReglasPage {

    @Override
    public String getTitle() {
        return "Rules Management (JettraRules)";
    }
}
