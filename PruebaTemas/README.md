## Ejecutar con CompactHeaders

```shell
 java -XX:+UseCompactObjectHeaders -jar target/FacliServer-1.0-SNAPSHOT.jar 

```

## Optimizar Arranque con Project Leyden


```shell
mvn clean verify
```

Ahora creamos el Cache calentando el arranque


```shell
java -XX:AOTCacheOutput=app_optimizada.aot -jar target/FacliServer-1.0-SNAPSHOT.jar 
```

Ejecuta la aplicación y presiona CTRL + C para detenerla
Muestra la salida con el archivo generando app_optimizada.aot 


Ahora para ejecutar la aplicación con el cache cargado debe copiar este archivo a al servidor donde será ejecutado

```shell
java -XX:AOTCache=app_optimizada.aot -jar target/FacliServer-1.0-SNAPSHOT.jar 
```



