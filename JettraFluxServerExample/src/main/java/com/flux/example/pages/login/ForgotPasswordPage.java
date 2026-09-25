package com.flux.example.pages.login;

import io.jettra.flux.pages.FluxBaseHandler;
import io.jettra.flux.widgets.Paragraph;
import io.jettra.flux.widgets.Scaffold;
import io.jettra.flux.widgets.ForgotPassword;
import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Notification;
import io.jettra.flux.widgets.Center;
import com.sun.net.httpserver.HttpExchange;
import io.jettra.core.server.Page;
import io.jettra.flux.core.Widget;
import io.jettra.server.JettraServer;

import java.util.Map;

@io.jettra.core.login.NoLoginRequired
@Page(path = "/forgot-password")
public class ForgotPasswordPage extends FluxBaseHandler {

    @Override
    protected String getTitle() {
        return "Forgot Password - JettraFlux";
    }

    @Override
    protected boolean onPost(HttpExchange exchange, Map<String, String> params) throws java.io.IOException {
        if (params.containsKey("email")) {
            String email = params.get("email");
            System.out.println("[ForgotPasswordPage] Reset link requested for: " + email);
            redirect(exchange, "/forgot-password?success=true");
            return true;
        }
        return false;
    }

    @Override
    protected Widget buildUI(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        Widget form = ForgotPassword.create().action(JettraServer.resolvePath("/forgot-password")).title("Reset Password").logo("https://primefaces.org/cdn/primeng/images/galleria/galleria1.jpg");
        
        Widget body = Center.of(
            Column.of(
                form
            )
        );

        if (params.containsKey("success")) {
            Widget successAlert = Notification.of(Paragraph.of("A password reset link has been sent to your email."));
            body = Center.of(
                Column.of(
                    successAlert,
                    form
                )
            );
        }

        return Scaffold.of().body(body);
    }
}
