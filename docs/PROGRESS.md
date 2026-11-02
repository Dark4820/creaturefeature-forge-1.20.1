# PROGRESS - Creature Feature port a Forge 1.20.1

## Estado actual
**F0 cerrada (análisis y esqueleto).** Esperando las decisiones D1–D4 de `PORT_MAP.md` §6 antes de empezar F1.

## F0 - Análisis y esqueleto (2026-11-02)
- Fuente vs jar: la fuente de GitHub es **más nueva** que el jar 1.2.3.3 (23 clases y 47 recursos más, 1 mixin más). Detalle en PORT_MAP §1.
- Inventario, mixins, diferencias 1.21.1→1.20.1, dependencias y plan: PORT_MAP §2–§8.
- Esqueleto Forge 1.20.1 (Forge 47.4.10, Gradle 8.8, ForgeGradle 6, mapeos oficiales):
  - Java 17 por toolchain: daemon fijado con `gradle/gradle-daemon-jvm.properties` y compilación con `java.toolchain` 17 + foojay. Verificado: con Java 25 en el `PATH` y sin `JAVA_HOME`, el build imprime `Java: 17.0.20.1`.
  - MixinExtras 0.4.1 por jarJar (`build\libs\creaturefeature-<v>-all.jar` es el jar para el juego).
  - `src/gametest` con `SkeletonGameTests.modIsLoaded` y la estructura `empty9x5x9`.
- Verificado: `gradlew.bat build` OK; `gradlew.bat runGameTestServer` → "All 1 required tests passed".
- **No verificado**: `runServer` hasta `Done` (requiere que el usuario acepte el EULA en `run\eula.txt`).

## Pendiente
- Decisiones D1–D4 (PORT_MAP §6).
- Usuario: aceptar el EULA en `run\eula.txt` para poder usar `runServer`.
