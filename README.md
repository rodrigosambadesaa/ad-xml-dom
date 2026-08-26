# XML DOM — lector y generador RSS

Reimplementación de la **Práctica XML: Lector y generador de noticias RSS**. El objetivo original era leer dos feeds, ordenar las noticias por fecha y generar HTML y XML.

La versión nueva:

- usa DOM con procesamiento XML endurecido contra XXE;
- acepta cualquier conjunto de feeds RSS 2.0;
- emplea `java.time` en lugar de `SimpleDateFormat` compartido;
- escapa HTML;
- genera XML mediante `Transformer`;
- incluye un feed local reproducible para CI.

Ejecute `RssAggregator input.xml output.html output.xml`.
