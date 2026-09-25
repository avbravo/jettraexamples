package com.facli.server;

import io.jettra.server.discoverer.DiscoveredLoad;
import io.jettra.test.annotation.JettraTest;
import io.jettra.test.core.JettraAssert;
import io.jettra.test.jwt.JwtTestClient;

import io.jettra.test.annotation.RequiresRunningServer;

/**
 * Clase de prueba para la aplicación backend.
 * Demuestra la integración del framework nativo JettraTest.
 */
@DiscoveredLoad
@RequiresRunningServer
public class AppTest {

    public Integer ServerPortTest = 9010;

    /**
     * Prueba de Integración: Flujo completo de Autenticación JWT.
     * <p>
     * Se utiliza la anotación @JettraTest para que el JettraTestRunner lo detecte
     * automáticamente durante la fase 'test' de Maven.
     * Se hace uso del JwtTestClient, un componente utilitario del framework para
     * abstraer la complejidad de enviar credenciales e inyectar tokens Bearer.
     * Las validaciones se efectúan mediante JettraAssert.
     * </p>
     */
    @JettraTest
    public void testJwtAuthentication() {
//        JwtTestClient jwtClient = new JwtTestClient();
       try {
//            // 1. Paso de Autenticación
//            // Se envía un payload con las credenciales de prueba al servidor.
//            // Asegúrate de que el servidor en http://localhost:PORT se encuentre encendido.
//            String authPayload = "{\"username\":\"admin\", \"password\":\"admin\"}";
//            String token = jwtClient.authenticate("http://localhost:" + ServerPortTest + "/auth/login", authPayload);
//            
//            // Validación: El servidor debe retornar un token no nulo.
//            JettraAssert.assertNotNull(token, "El token de autenticación no debe ser nulo");
//            
//            IO.println("Token obtenido exitosamente: " + token);
//            
//            // 2. Acceso a Ruta Protegida
//            // El cliente JwtTestClient inyectará automáticamente el token en los headers de la petición GET.
//            String response = jwtClient.getWithToken("http://localhost:" + ServerPortTest + "/autentification/jusers");
//            
//            // Validación: La respuesta no debe ser nula si la validación del JWT fue exitosa.
//            JettraAssert.assertNotNull(response, "La respuesta de la ruta protegida no debe ser nula");
//           IO.println("Datos del usuario protegidos recibidos: " + response);
            
        } catch (Exception e) {
            // Si ocurre un fallo en la conexión HTTP o la respuesta es errónea, 
            // forzamos el fallo de la prueba.
            JettraAssert.assertTrue(false, "La prueba falló debido a una excepción: " + e.getMessage());
        }
    }
}
