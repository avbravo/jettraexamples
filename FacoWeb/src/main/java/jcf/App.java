package jcf;

import io.jettra.rest.server.JettraRestServer;
import io.jettra.server.JettraServer;
import io.jettra.server.config.ConfigInjector;
import io.jettra.server.config.JettraConfigProperty;
import io.jettra.server.discoverer.DiscoveredLoad;
import io.jettra.server.openapi.OpenApiHandler;
import io.jettra.server.openapi.SwaggerUIHandler;
import java.util.List;

@DiscoveredLoad
public class App {

    @JettraConfigProperty(name = "app.title")
    private String appTitle;
    @JettraConfigProperty(name = "server.port")
    private String port;
    @JettraConfigProperty(name = "server.contextpath")
    private String contextpath;
    public static JettraServer serverInstance;

    public void initUI() {
        ConfigInjector.inject(this);
        System.out.println("Iniciando aplicación Web: " + appTitle);
    }

    public static void main(String[] args) {
        if (args != null && args.length > 0 && args[0].equals("-console")) {
            io.jettra.server.autentification.SecurityCLI.main(args);
            return;
        }
        if (args != null && args.length > 0 && args[0].equals("-generate-flux-jettra-sh")) {
            io.jettra.server.JettraServer.generateMvnScripts();
            return;
        }

        App app = new App();
        app.initUI();
        io.jettra.flux.complex.ErrorPage.path = "http://localhost:" + app.port + app.contextpath;

        System.out.println("Levantando servidor de enrutamiento JettraServer empotrado...");
        JettraServer server = new JettraServer();
        server.setErrorPage("/error");
        server.addHandler("/error", io.jettra.flux.complex.ErrorPage.class);
        server.addHandler("/swagger-ui", io.jettra.flux.complex.SwaggerUIPage.class);

        // Registro de Páginas JettraFlux
        server.addHandler("/", jcf.login.LoginPage.class);

        List<Class<?>> controllers = new java.util.ArrayList<>(io.jettra.server.discoverer.DiscoveredRegistry.getDiscoveredClasses(App.class));

        server.addHandler("/openapi.json", new OpenApiHandler(controllers));
        server.addHandler("/swagger-ui", new SwaggerUIHandler("/openapi.json"));

        JettraRestServer.registerDiscovered(server, App.class);

        server.start();
    }
}
