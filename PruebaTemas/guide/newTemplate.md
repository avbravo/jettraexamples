Implementación de Comandos de Generación de Plugins en JettraAppServer
Este plan describe cómo se implementará el generador de plugins JettraFlux (creación, instalación y eliminación) basado en el proyecto JettraFluxPluginExample.

User Review Required
IMPORTANT

Sintaxis de Maven: El comando mvn -jettra no es una sintaxis nativa válida para Maven (Maven reportará "Unrecognized option: -jettra"). Para resolver esto, propongo dos alternativas para la implementación:

Opción A (Recomendada): Crear un Maven Plugin personalizado (jettra-maven-plugin). El comando a ejecutar sería: mvn jettra:generate-plugin -DpluginName=MiPlugin -DexcludePlugin=plugin1,plugin2

Opción B: Crear un script de consola (jettra.sh y jettra.bat) o ejecutar una clase CLI de Java con exec-maven-plugin. Ej: mvn exec:java -Dexec.mainClass="io.jettra.server.cli.PluginCLI" -Dexec.args="generate-plugin MiPlugin" o ejecutar directamente un script ./jettra generate-plugin MiPlugin

¿Cuál de estas alternativas prefieres? El plan a continuación asume que construiremos una utilidad CLI interna en Java que será la encargada de hacer el andamiaje (scaffolding) de los archivos.

Propuesta de Cambios
La lógica de andamiaje (scaffolding) se construirá dentro de JettraAppServer (por ejemplo en un paquete io.jettra.server.cli). Se creará una clase PluginGeneratorCLI que se encargará de realizar las operaciones de copia, filtrado y reemplazo de cadenas de texto.

1. Motor de Generación (JettraAppServer)
[NEW] io.jettra.server.cli.PluginGeneratorCLI: Clase principal que recibirá los argumentos (generate-plugin, install-plugin, remove-plugin, exclude-plugin).
[NEW] io.jettra.server.cli.generator.ProjectScaffolder: Clase encargada de clonar la estructura de JettraFluxPluginExample a una nueva carpeta.
Acciones de generate-plugin <nombre-plugin>
Creación del POM: Se generará un nuevo pom.xml con:
groupId: io.jettraflux.<nombre-plugin-minuscula>
artifactId: <nombre-plugin>
Se clonarán <properties>, <dependencies> y <build> del proyecto ejemplo.
Se excluirá la propiedad <main.class.path>.
Exclusión de Archivos:
App.java, DashboardPage.java, LoginPage.java, ForgotPasswordPage.java.
jettra-config.properties, jettra-rest.properties.
Manejo de TemplatePage.java:
No será copiada. En su lugar, el generador creará el archivo plugin-descriptor.md extrayendo los menús (WidgetLet) y configuraciones encontradas estáticamente en el TemplatePage de ejemplo, para futura referencia de generación.
Tratamiento de messages.properties:
Se buscarán archivos messages*.properties (ej. messages_es.properties) y se renombrarán a messages-<nombre-plugin>_es.properties (o según corresponda a la convención messages-<nombre-plugin>.properties).
El generador escaneará recursivamente todos los archivos .java en el nuevo proyecto. Se buscarán las ocurrencias de @InjectProperties(name = "messages") y serán reemplazadas por @InjectProperties(name = "messages-<nombre-plugin>").
2. Documentación (JettraAppServer)
[NEW] JettraAppServer/guide/plugin-create.md: Se creará esta guía detallando los comandos, argumentos permitidos y la arquitectura del generador, además de instrucciones para su uso desde consola.
Verification Plan
Manual Verification
Compilar el generador en JettraAppServer.
Ir a la carpeta del proyecto JettraFluxExample.
Ejecutar el comando de generación propuesto: mvn exec:java ... "generate-plugin JettraFluxExampleBasico" (o el equivalente según la opción elegida por el usuario).
Verificar que se ha creado la carpeta JettraFluxExampleBasico.
Verificar el pom.xml (validar grupo, artefacto y exclusión de main.class.path).
Verificar que las clases y propiedades excluidas efectivamente no existen.
Comprobar la creación de plugin-descriptor.md.
Validar el renombramiento de messages-<nombre-plugin> y el reemplazo de las anotaciones @InjectProperties en el código fuente.
Compilar el nuevo plugin (mvn clean install) para garantizar que la reestructuración no ha roto el código generado.