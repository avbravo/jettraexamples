package com.flux.example.pages.login;

import io.jettra.flux.pages.FluxBaseHandler;
import io.jettra.flux.widgets.Paragraph;
import io.jettra.flux.widgets.Scaffold;
import io.jettra.flux.widgets.Login;
import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Notification;
import io.jettra.flux.widgets.Center;
import com.sun.net.httpserver.HttpExchange;
import io.jettra.core.server.Page;
import io.jettra.flux.core.Widget;
import io.jettra.flux.model.CredentialFlux;
import io.jettra.json.SessionScoped;
import io.jettra.server.JettraServer;

import java.util.Map;

@io.jettra.core.login.NoLoginRequired
@SessionScoped
@Page(path = "/login")
public class LoginPage extends FluxBaseHandler {

    @Override
    protected String getTitle() {
        return "Login - JettraFlux";
    }

    @Override
    protected boolean onPost(HttpExchange exchange, Map<String, String> params) throws java.io.IOException {
        String user = params.get("username");
        String pass = params.get("password");
        if (user == null || user.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
            redirect(exchange, "/login?error=empty_fields");
            return true;
        }
        if (isValidUser(user, pass)) {
            String role = user.equals("demo")?"DEMO":"ADMIN";
//            CredentialFlux credentialFlux = new CredentialFlux(user, user + "Prueba", "ADMIN", "", "");
            CredentialFlux credentialFlux = new CredentialFlux(user, user + "Prueba", role, "", "");
            io.jettra.server.core.JettraContext.getCurrent().set(io.jettra.server.core.JettraContext.Scope.SESSION, "credentialFlux", credentialFlux);
            setSessionCookie(exchange, user, credentialFlux.role(), credentialFlux.department());
            redirect(exchange, "/dashboard");
            return true;
        } else {
            redirect(exchange, "/login?error=invalid_credentials");
            return true;
        }
    }

    @Override
    protected boolean onGet(HttpExchange exchange, Map<String, String> params) throws java.io.IOException {
        if ("true".equals(params.get("logout"))) {
            clearSessionCookie(exchange);
            redirect(exchange, "/login");
            return true;
        }
        return false;
    }

    @Override
    protected Widget buildUI(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        Widget loginForm = Login.create().action(JettraServer.resolvePath("/login")).title("JettraFlux Admin").logo("https://primefaces.org/cdn/primeng/images/galleria/galleria1.jpg").forgotPasswordUrl(JettraServer.resolvePath("/forgot-password"));
        
        Widget body = Center.of(
            Column.of(
                loginForm
            )
        );

        if (params.containsKey("error")) {
            String errorParam = params.get("error");
            String titleStr = "Advertencia de Autenticación";
            String msgStr = "El nombre de usuario o la contraseña ingresados no son válidos. Por favor verifique sus credenciales.";
            
            if ("empty_fields".equals(errorParam) || "empty".equals(errorParam)) {
                titleStr = "Campos Requeridos";
                msgStr = "Error: Username y password son requeridos.";
            }

            Widget notificationBanner = Notification.of(
                Paragraph.of(msgStr).modifier(new io.jettra.flux.core.Modifier().style("margin: 0; color: #b91c1c; font-weight: 600;"))
            ).modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 15px; padding: 12px 16px; background-color: #fef2f2; border: 1px solid #fca5a5; border-radius: 8px; width: 100%; max-width: 400px; box-sizing: border-box;"));

            Widget errorDialog = io.jettra.flux.widgets.Modal.of(
                Column.of(
                    io.jettra.flux.widgets.Header.of(4, titleStr)
                            .modifier(new io.jettra.flux.core.Modifier().style("color: #b91c1c; margin-top: 0; margin-bottom: 10px; font-weight: 600;")),
                    Paragraph.of(msgStr)
                            .modifier(new io.jettra.flux.core.Modifier().style("margin-bottom: 20px; color: #374151; font-size: 14px;")),
                    io.jettra.flux.widgets.ElevatedButton.of("Aceptar")
                            .attribute("type", "button")
                            .attribute("onclick", "this.closest('.espresso-modal-overlay').style.display='none'; return false;")
                            .modifier(new io.jettra.flux.core.Modifier().style("background-color: #ef4444; color: white; border: none; padding: 8px 20px; border-radius: 6px; cursor: pointer; font-weight: 500; align-self: flex-end;"))
                ).modifier(new io.jettra.flux.core.Modifier().style("width: 100%; max-width: 400px; gap: 10px;"))
            ).open(true);

            body = Center.of(
                Column.of(
                    notificationBanner,
                    errorDialog,
                    loginForm
                )
            );
        }

        return Scaffold.of().body(body);
    }

    private boolean isValidUser(String user, String pass) {
        return ("admin".equals(user) && "admin".equals(pass)) || 
               ("demo".equals(user) && "demo".equals(pass)) || 
               ("avbravo".equals(user) && "avbravo".equals(pass));
    }
}
