package com.flux.example;

import io.jettra.rest.server.JettraRestServer;
import io.jettra.server.JettraServer;
import io.jettra.server.config.ConfigInjector;
import io.jettra.server.config.JettraConfigProperty;
import io.jettra.server.discoverer.DiscoveredLoad;
import io.jettra.server.openapi.OpenApiHandler;
import io.jettra.server.openapi.SwaggerUIHandler;
import java.util.List;

/**
 * App!
 *
 */
/**
 * Hello world!
 *
 */
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
        IO.println("Iniciando aplicación Web: " + appTitle);
    }

    public static void main(String[] args) {
        if (args != null && args.length > 0 && args[0].equals("-console")) {
            io.jettra.server.autentification.SecurityCLI.main(args);
            return;
        }

        App app = new App();
        app.initUI();
        // Configurar la ruta de redirección en ErrorPage, usando contextpath (y el puerto implícitamente por el host)
        io.jettra.flux.complex.ErrorPage.path = "http://localhost:" + app.port + app.contextpath;

        IO.println("Levantando servidor de enrutamiento JettraServer empotrado...");
        JettraServer server = new JettraServer();
        server.setErrorPage("/error");
        server.addHandler("/error", io.jettra.flux.complex.ErrorPage.class);
        server.addHandler("/swagger-ui", io.jettra.flux.complex.SwaggerUIPage.class);

        // Registro de Páginas JettraFlux
        server.addHandler("/", com.flux.example.pages.login.LoginPage.class);

        // Cargamos los controladores descubiertos automáticamente
        List<Class<?>> controllers = new java.util.ArrayList<>(io.jettra.server.discoverer.DiscoveredRegistry.getDiscoveredClasses(App.class));

        // Puedes agregar aquí manualmente las clases que tengan @Discovered(automatic=false)
        // o que no tengan la anotación
        // controllers.add(MiControladorManual.class);
//        controllers.add(com.flux.example.controller.ContenedorMaritimoController.class);
// Exponer el JSON de OpenAPI
        server.addHandler("/openapi.json", new OpenApiHandler(controllers));

        // Exponer la interfaz Swagger UI
        server.addHandler("/swagger-ui", new SwaggerUIHandler("/openapi.json"));

        // Registrar los controladores descubiertos en JettraRestServer
        JettraRestServer.registerDiscovered(server, App.class);

        // Registro manual para los que no se descubren automáticamente
//        JettraRestServer.register(server, AuthController.class);
        server.start();

    }

}
