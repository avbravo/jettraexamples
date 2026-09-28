# Empleando una imagen ligera orientada a un runtime óptimo y moderno
# Usando BellSoft Liberica JRE con Alpaquita Linux para menor tamaño y mayor rendimiento
FROM bellsoft/liberica-runtime-container:jre-25-stream-musl

LABEL maintainer="Jettra Development Team"
LABEL description="Contenedor de Demostración JettraBackEnd"

WORKDIR /app

# Copia el JAR compilado desde target de su entorno en Host
COPY target/FacliServer-1.0-SNAPSHOT.jar  /app/FacliServer.jar

# Entrenar la JVM para generar el AOT Cache (AppCDS)
# Ejecutamos la app por 10 segundos para cargar las clases y luego forzamos la salida guardando el archivo .jsa
RUN timeout 30s java -XX:ArchiveClassesAtExit=app.jsa -jar FacliServer.jar || true

# Exponer el puerto por defecto (configurable via -e variables de entorno o property config)
#EXPOSE 8080
EXPOSE 9010

# Documenta que el contenedor utiliza persistencia en este directorio
VOLUME ["/app/db"]

# Opciones de JVM para optimización de rendimiento en contenedores y habilitación de AOT Cache
ENV JAVA_OPTS="-XX:+UseCompactObjectHeaders -XX:+UseZGC -XX:+ZGenerational -Xmx512m -XX:MaxRAMPercentage=75.0 -XX:SharedArchiveFile=app.jsa"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar FacliServer.jar"]

# Configuracion para el Shell de JettraSecurityDB
CMD []