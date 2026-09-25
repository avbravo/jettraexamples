# JettraFlux - Componentes Diversos (Misc)

Este documento detalla el uso de los componentes diversos que se muestran en la página `MiscPage` de JettraFlux.

## ProgressBar
El componente `ProgressBar` permite mostrar una barra de progreso que indica el porcentaje de avance de una tarea.

```java
ProgressBar.of()
    .value(50)
    .max(100) // Opcional, por defecto es 100
    .modifier(new Modifier().style("width: 100%; height: 20px; border-radius: 6px;"));
```

## Badge
Los `Badge` son pequeñas insignias utilizadas para denotar contadores o estados. Se pueden colocar de manera independiente o adjuntos a un icono/avatar.

```java
// Badge independiente
Badge.of("8").severity("success");

// Badge adjunto a un Avatar
Avatar.icon("fas fa-envelope").size("large").badge(Badge.of("10").severity("danger"));
```
*Severidades soportadas:* `primary`, `success`, `info`, `warning`, `danger`.

## Tag
El componente `Tag` permite categorizar información. Admite texto, iconos y diferentes colores de severidad.

```java
// Tag simple
Tag.of("Primary").severity("primary");

// Tag con icono
Tag.of("Success").severity("success").icon("fas fa-check");
```
*Severidades soportadas:* `primary`, `success`, `info`, `warning`, `danger`.

## Avatar
El `Avatar` se usa para representar usuarios. Puede mostrar una imagen, un icono o las iniciales del usuario. También soporta formas (círculo o cuadrado) y agrupaciones con `AvatarGroup`.

```java
// Avatar con Imagen
Avatar.image("https://.../amyelsner.png").shape("circle").size("large");

// Avatar con Texto (Iniciales)
Avatar.label("P").shape("circle").size("large");

// Avatar con Icono
Avatar.icon("fas fa-user").shape("circle").size("large");

// Avatar Group
AvatarGroup.of(
    Avatar.image("url1").shape("circle"),
    Avatar.image("url2").shape("circle"),
    Avatar.label("+2").shape("circle")
);
```

## Chip
Los `Chip` son pequeños bloques que representan información compleja como un contacto, opción, o etiqueta con una imagen y botón de cerrar.

```java
// Chip básico
Chip.of("Action");

// Chip removible
Chip.of("Thriller").removable(true);

// Chip con Icono
Chip.of("Settings").icon("fas fa-cog");

// Chip con Imagen
Chip.of("Amy Elsner").image("https://.../amyelsner.png");
```

## ScrollTop
El componente `ScrollTop` renderiza un botón flotante que, al presionarlo, permite hacer scroll hacia arriba de la pantalla. Aparece solo cuando el usuario ha descendido cierta distancia en la página.

```java
ScrollTop.of();
```

## Skeleton
El componente `Skeleton` es útil como "placeholder" o marcador visual mientras la información se está cargando.

```java
// Skeleton circular
Skeleton.circle("4rem");

// Skeleton rectangular (ancho, alto)
Skeleton.of("10rem", "1rem");
```

## MeterGroup
El `MeterGroup` permite visualizar varias métricas proporcionales en una sola barra compuesta por varios segmentos de colores diferentes.

```java
MeterGroup.of()
    .add("Space used", 15, "#3b82f6")
    .add("System", 20, "#eab308")
    .add("Media", 40, "#ec4899")
    .add("Apps", 10, "#8b5cf6");
```
