package com.facli.server;

import com.flux.example.App;
import io.jettra.test.annotation.JettraTestLauncher;

@JettraTestLauncher
public class TestLauncher {

    public void startServer(int port) {
        IO.println("[TestLauncher] Configurando servidor de prueba en puerto: " + port);
        // Override properties to start in test mode on the dynamic port
        System.setProperty("server.port", String.valueOf(port));

        // This simulates JettraConfig seeing the property, if JettraConfig uses it.
        // Also App reads it via @JetraConfigProperty, but App doesn't read System properties automatically unless ConfigInjector supports it.
        // However, JettraServer has setPort(port), we can just set it on App's logic if needed, but since we modified App to have serverInstance
        // actually we just run App.main and maybe we need to override the port in JettraServer?
        // JettraServer reads: io.jettra.server.config.JettraConfig.getProperty("server.port")
        // If we can't easily mock it, we can create App, call initUI, and set it.
        // Let's run App.main and then if we need to force port:
        // Actually, JettraConfig in JettraServer might read system properties. Let's just run App.main() and hope JettraConfig handles it,
        // or we manually set it if App provides a way. Let's run App.main() and use the System property.
        // Another option: start App in a new thread so it doesn't block the launcher method.
        Thread t = new Thread(() -> {
            try {
                // If App reads from properties file, we can't easily override unless we can inject.
                App app = new App();
                app.initUI();

                // Set the port manually before starting
                IO.println("[TestLauncher] Levantando servidor JettraServer...");
                App.serverInstance = new io.jettra.server.JettraServer();
                App.serverInstance.setPort(port); // Override port!
                App.serverInstance.setErrorPage("/error");
                App.serverInstance.addHandler("/error", io.jettra.flux.complex.ErrorPage.class);
                App.serverInstance.addHandler("/swagger-ui", io.jettra.flux.complex.SwaggerUIPage.class);

                java.util.List<Class<?>> controllers = new java.util.ArrayList<>(io.jettra.server.discoverer.DiscoveredRegistry.getDiscoveredClasses(App.class));
                App.serverInstance.addHandler("/openapi.json", new io.jettra.server.openapi.OpenApiHandler(controllers));
                App.serverInstance.addHandler("/swagger-ui", new io.jettra.server.openapi.SwaggerUIHandler("/openapi.json"));

                io.jettra.rest.server.JettraRestServer.registerDiscovered(App.serverInstance, App.class);

                App.serverInstance.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();

        // Give it a moment to bind
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
    }

    public void stopServer() {
        IO.println("[TestLauncher] Deteniendo servidor de prueba...");
        if (App.serverInstance != null) {
            App.serverInstance.stop();
            App.serverInstance = null;
        }
    }
}
