# Libro de Referencia: JettraFlux y JettraFluxExample

## Arquitectura del Catálogo de Componentes

La arquitectura visual de `JettraFlux` está diseñada sin requerir que los desarrolladores manipulen plantillas HTML crudas o estilos en línea directamente (a menos que diseñen el core del framework). La interfaz se construye utilizando clases y constructores fluidos, generando el HTML/CSS bajo el capó.

### Componentes Core:
- **`WidgetLet`**: Herramienta principal para estructurar menús jerárquicos o de navegación, encapsulando títulos, iconos, redirecciones y sub-menús.
- **`Scaffold`**: Plantilla base (layout) para aplicaciones web, soportando una barra superior (`TopBar`), área de contenido central (`Body`) y balanceo visual programático, incluyendo de manera nativa un menú tipo hamburguesa cuando las resoluciones de pantalla decrecen a vista de móvil (< 768px).

## Esquema de Seguridad Declarativa

### 1. Autenticación y Control de Acceso por Defecto
Todas las páginas servidas están bloqueadas de forma predeterminada mediante el `FluxBaseHandler`. Sólo aquellas explícitamente excluidas mediante `@NoLoginRequired` están exentas del chequeo.

### 2. Anotaciones de Nivel Fino
- `@PageWidgetAllow(role, department)`: Se utiliza a nivel de clase de vista. Define qué roles o departamentos pueden acceder. 
- `@ActionWidgetAllow(role, department)`: Se utiliza a nivel de método o campo de un componente, controlando la lógica y permitiendo deshabilitar o inhibir llamadas sensibles (por ejemplo, clicks o envío de formularios) según la información de `FluxLogin` y `CredentialFlux`.

## Gestión de Sesión

### `FluxLogin` y Expiración
El contexto `FluxLogin` maneja el estado del usuario autenticado en la plataforma. Posee soporte integrado para tiempos de caducidad. Si el usuario realiza una petición tras el fin de este periodo, la sesión se destruirá, se renderizará un componente de notificación (modal o script) alertando de la expiración y se realizará una redirección automática forzosa a la pantalla de Login.

*El motor de expiración y ciclos de vida también cuenta con soporte de recolección de basura mediante un hilo en `JettraAppServer` y `JettraContext`.*
