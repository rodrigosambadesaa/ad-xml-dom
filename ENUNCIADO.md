# Enunciado original — Práctica XML: lector y generador de noticias RSS

Fuente: `PRÁCTICA XML.pdf` de **Acceso a Datos**.

## Objetivo

Procesar mediante DOM las noticias de portada publicadas en formato RSS por dos periódicos —en la práctica original, **La Razón** y **El País**—, reunirlas en una colección, **ordenarlas por fecha de publicación más reciente** y generar a partir de ellas:

- un documento **HTML5** con las noticias;
- un nuevo documento **XML/RSS**.

## Modelo `NoticiaRSS`

Cada noticia debe representarse mediante un objeto con, al menos:

- título (`<title>`),
- descripción (`<description>`),
- enlace (`<link>`),
- fecha de publicación (`<pubDate>`).

El constructor recibe un elemento DOM `<item>` y extrae sus datos. La clase debe permitir transformar las distintas variantes de fecha RSS a `Calendar` y a una cadena formateada.

## Generación de salida

La práctica solicita métodos para:

1. convertir una noticia a fragmento HTML;
2. añadir una noticia como `<item>` a un árbol DOM RSS;
3. generar el fichero HTML con estructura HTML5;
4. serializar el árbol DOM a XML.

### Mejora propuesta

Analizar cómo los dos canales RSS incorporan las imágenes de las noticias e intentar incluirlas también en el HTML generado.

No existe una base de datos asociada: la fuente de datos son documentos RSS/XML.
