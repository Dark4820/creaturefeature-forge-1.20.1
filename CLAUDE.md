# CLAUDE.md - Creature Feature: port personal a Forge 1.20.1

## Qué es esto
Port **personal** de *Creature Feature* (AtiredDev / Team Galena, NeoForge 1.21.1, licencia **MIT**) a **Forge 1.20.1** para el modpack del usuario.
Antes de cada tramo hay que leer `docs\PORT_MAP.md` (inventario, diferencias, fases) y `docs\PROGRESS.md` (estado), y **actualizar PROGRESS al terminar**.

## Licencia
- El original declara MIT en sus metadatos (`neoforge.mods.toml`); el repositorio no trae archivo `LICENSE`. Se mantiene `mod_license=MIT` y los créditos a AtiredDev / Team Galena en `mods.toml`.
- Uso personal: **git solo local** (ver "Cómo trabajar").

## Carpetas
- `C:\cf\port` = este proyecto (lo único que se edita). Rutas cortas por el límite de 260 caracteres de Windows.
- `C:\cf\ref` = **solo lectura**:
  - `creaturefeature-master\creaturefeature-master\` = código fuente de GitHub (TerriblyBadCoder/creaturefeature), NeoForge 1.21.1. **Es más nuevo que el jar** aunque diga `mod_version=1.2.3.3` (ver PORT_MAP, "Fuente vs jar").
  - `creaturefeature-1.2.3.3.jar` = el jar publicado (trae `META-INF/jarjar/hats-neoforge-1.21.1-1.2.5.jar`).

## Entorno (Windows 11)
- Minecraft 1.20.1, **Forge 47.4.10**, mapeos oficiales, Gradle 8.8 + ForgeGradle 6.
- **Java 17 siempre, por toolchain de Gradle**: la PC solo tiene Java 25 en el `PATH`. El daemon de Gradle se fija en 17 con `gradle/gradle-daemon-jvm.properties` (`toolchainVersion=17`) y la compilación con `java.toolchain` 17 + plugin foojay (descarga el JDK solo si falta; hoy usa el de `%USERPROFILE%\.gradle\jdks`). **No** hace falta tocar `JAVA_HOME`. La primera línea del build imprime `Java: 17...`: si dice otra cosa, frenar.
- Compilar: `gradlew.bat build --console=plain`. Pruebas automáticas: `gradlew.bat runGameTestServer` (viven en `src/gametest`, conjunto de fuentes solo de desarrollo; lo cargan todas las ejecuciones pero **no entra en el jar**).
- **El resultado de `runGameTestServer` es la línea `All N required tests passed`**, no el `BUILD SUCCESSFUL` de Gradle: si el mod no carga (excepción en `common_setup`), el servidor de pruebas se cierra y Gradle igual dice BUILD SUCCESSFUL. Buscar también `Exception caught during firing event`.
- Probar que carga: `gradlew.bat runServer` y leer `run\logs\latest.log` hasta ver `Done` (no esperar la salida de la consola).
- **Si la fase toca recursos de cliente** (shaders, partículas, modelos, texturas, atlas), además `gradlew.bat runClient` hasta la pantalla de título y buscar en el log errores del mod (`Couldn't compile`, `Redundant texture list`, `Failed to load model`, crash reports). El servidor no carga nada de eso. Cerrar el cliente por su PID (proceso con `forgeclientuserdev` y `C:\cf\port` en la línea de comandos).
- **Dibujo de las criaturas:** `gradlew.bat runClientRenderSmoke` (solo desarrollo) abre el juego en `run-smoke`, crea un mundo plano, invoca todas las criaturas y cierra solo: buscar `CF_RENDER_SMOKE_OK` en la salida (o un crash report en `run-smoke\crash-reports`) y mirar las capturas de `run-smoke\screenshots`. Borrar `run-smoke\saves` antes de cada corrida. Correrlo en cada fase que agregue o toque renderers. Desde F7 también fuerza cada efecto de pantalla en Rápidos, Elegantes y Fabulosos (`cf_fx_<modo>_<efecto>.png`). Para cambiar el modo gráfico por código: `graphicsMode().set(...)` y después `levelRenderer.allChanged()` (la opción sola no reconstruye; sin eso Fabulosos crashea).
- **El EULA lo acepta el usuario, nunca Claude** (`run\eula.txt`).
- Puede haber **otros Claude Code trabajando a la vez en otros proyectos**: NUNCA cerrar procesos por nombre (`java.exe`). Cerrar solo el PID que este proyecto inició y verificar solo ese PID. Si el puerto 25565 está ocupado, usar otro en `run\server.properties`.
- El jar para el modpack es `build\libs\creaturefeature-<versión>-all.jar` (lleva MixinExtras adentro por jarJar). El jar sin `-all` no sirve en el juego real.
- Recursos: `python -I tools/convert_resources.py` copia los assets y convierte recetas, loot tables de bloques y tags de la fuente al formato 1.20.1. Solo escribe lo que ya está registrado en `CFItemInit`/`CFBlockInit` y lista lo saltado: volver a correrlo en cada fase. Los atlas de `assets/minecraft/atlases` están hechos a mano (no los toca).
- Bytecode del jar de referencia: `javap -p -c -classpath C:\cf\ref\creaturefeature-1.2.3.3.jar <clase>`.

## Cómo trabajar
1. Por **fases** (ver PORT_MAP). Cada fase se cierra con: compila, **GameTests pasan** (`runGameTestServer`), **`runServer` llega a `Done`** sin errores del mod, y una **lista corta de pruebas dentro del juego** con comandos concretos. Después, **commit local** con un mensaje que nombre la fase.
2. **Español** con el usuario; código y comentarios en inglés. Reportar corto: qué se hizo, qué se compiló, qué falta probar.
3. **Mantener paquetes, clases y nombres de registro del original** (`net.atired.creaturefeature...`, `creaturefeature:<id>`) para poder comparar archivo por archivo y no romper mundos.
4. **Todo cambio respecto del original lleva un comentario `// PORT: <motivo>`** (en JSON, que no admite comentarios, anotarlo en PORT_MAP).
5. **No inventar APIs.** Si no se sabe cómo se llama algo en 1.20.1, escribirlo y dejar que el compilador lo confirme; para mixins, mirar la clase real de 1.20.1 (nombres, firmas y ordinales cambian).
6. **Nada de accesos por nombre que fallen en el jar real**: el juego real está ofuscado (SRG). Prohibido `getDeclaredField("nombreMojang")`, `Class.forName` de clases de Minecraft o reflexión por nombre sin `ObfuscationReflectionHelper` con el nombre SRG. Preferir mixin `@Accessor`/`@Invoker` (con refmap) o un Access Transformer.
7. **Lo de cliente solo en clases de cliente**: el código común nunca nombra clases de `net.minecraft.client` ni `CreatureFeatureClient`. Los manejadores de paquetes de cliente y el `PROXY` van en clases aparte, llamadas con `DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ...)`. Verificarlo con `runServer`/`runGameTestServer`. (El original mezcla cosas, p. ej. `JukeBoxPlayableMixin` y `CreatureFeature` importan clases de cliente: corregirlo al portar.)
8. Sesiones cortas por fase.
9. **Git solo local:** identidad propia del repo ("CF Port" / cf@local). **Nunca agregar un remoto ni hacer push.** El `.gitignore` excluye `build/`, `run/`, `.gradle/`, los jars (salvo el del wrapper) y los logs.

## Reglas técnicas de 1.20.1 (mismo entorno que los otros ports)
- **Datos:** carpetas en plural: `recipes/`, `loot_tables/`, `advancements/`, `tags/blocks|items|entity_types/`, `structures/`. JSON con `"item"` (no `"id"`), sin `components`; condiciones y tags `forge:` (no `neoforge:`/`c:`). Biome modifiers en `data/<ns>/forge/biome_modifier/` con `forge:add_spawns`/`forge:remove_spawns`.
- **Loot modifiers:** Forge 1.20.1 **no trae `add_table`**: hay que escribir un `IGlobalLootModifier` propio (codec) y registrarlo. La condición `forge:loot_table_id` sí existe.
- `new ResourceLocation(ns, path)`. Eventos: `MinecraftForge.EVENT_BUS` y `FMLJavaModLoadingContext.get().getModEventBus()`.
- **Red:** `SimpleChannel` (no `CustomPacketPayload`/`StreamCodec`). Paquetes C2S: validar en el servidor lo que manda el cliente.
- **Componentes de ítem (1.20.5+) no existen:** usar NBT (`getOrCreateTag()`).
- **`Holder<>`**: efectos, sonidos y atributos se usan directos (`RegistryObject.get()`). Modificadores de atributo con `UUID`, no `ResourceLocation`.
- **Shaders de entidad 1.20.1:** las posiciones llegan rotadas por la cámara (`IViewRotMat` las vuelve a ejes del mundo) y `fog_distance(ModelViewMat, pos, shape)` lleva 3 parámetros. Ver PORT_MAP §6b.
- **Partículas:** todo `particles/<id>.json` necesita su tipo registrado y su provider, o falla la recarga del cliente. `SingleQuadParticle.renderRotatedQuad` no existe: usar `CFTextureSheetParticle`.
- **Java 17:** nada de APIs de Java 21 (`Math.clamp` → `Mth.clamp`, `List.getFirst()` → `get(0)`, etc.).
- **Archivos hechos a mano** que el script de recursos no toca: los atlas (`assets/minecraft/atlases`), `data/minecraft/tags/items/music_discs.json`, `data/forge/tags/items/ectoplasm.json`, `data/creaturefeature/recipes/compat/` y los de `HAND_EDITED` en el script.
- **Medición de rendimiento (solo desarrollo):** `gradlew.bat runGameTestServer -Dcreaturefeature.perf=true` corre además `PerfGameTests` (tiempo de tick del nivel con 1/10/30 zombis, pathogens y blossoms); resultados en las líneas `CF_PERF` del log.
- **Buffers compartidos 1.20.1:** pedir un render type nuevo a un `MultiBufferSource` (o dibujar ítems/bloques con él) cierra el lote anterior; un `VertexConsumer` pedido antes queda inválido ("BufferBuilder not started"). Pedir el consumer justo antes de escribirle. Los buffers propios (`immediateWithBuffers`) los inicia el propio `BufferSource`, pero alguien tiene que llamar a `endBatch()`.
- **VertexConsumer 1.20.1:** `vertex(...).color(...).uv(...).overlayCoords(...).uv2(...).normal(...).endVertex()`; `renderToBuffer` recibe `r, g, b, a` como floats (no un `int` ARGB).
- `EntityType.Builder` no tiene `eyeHeight`/`passengerAttachments`/`ridingOffset`: van como overrides en la entidad. `defineSynchedData()` sin parámetros.
- Configuración con `ForgeConfigSpec`; registros con `DeferredRegister`/`RegistryObject`.
- `Item.Properties`: nunca `stacksTo` después de `durability` (en 1.20.1 lanza "Unable to have damage AND stack"); `durability` ya deja el stack en 1.
- Entidades (ver PORT_MAP §6c): altura de ojos y monturas como overrides; gravedad propia → atributo `ForgeMod.ENTITY_GRAVITY`; `STEP_HEIGHT` → `setMaxUpStep`; huevos con `ForgeSpawnEggItem`; `Entity.getAddEntityPacket()` es abstracto en 1.20.1.
- Mixins: todo mixin nuevo va en `src/main/resources/creaturefeature.mixins.json`. MixinExtras 0.4.1 está disponible (jarJar). Preferir un evento de Forge a un mixin.
