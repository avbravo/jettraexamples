package com.flux.example.pages;

import com.flux.example.pages.dashboard.DashboardPage;
import io.jettra.test.annotation.JettraTest;
import io.jettra.test.core.JettraAssert;

public class DashboardPageTest {
    @JettraTest
    public void testTitle() {
        DashboardPage page = new DashboardPage();
   //     JettraAssert.assertEquals("Dashboard - JettraFlux Pro", page.getTitle(), "Title should match");
    }
}
