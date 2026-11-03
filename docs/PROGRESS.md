# PROGRESS - Creature Feature port a Forge 1.20.1

## Estado actual
**F5 cerrada (2026-11-02).** Falta la prueba corta de F5 y volver a probar coat of arms y friend. Siguiente: F6 (ítems con lógica y mixins comunes).

## Pruebas en el juego pendientes
- **Arreglos de F4:** coat of arms (abre la capa y dispara sin crashear) y friend (se ve el cubo, la carita y las copias), en gráficos Rápidos/Detallados y en Fabulosos.
- **Prueba corta de F5:** que las 8 criaturas, los proyectiles y el eep se vean, se animen y ataquen bien (lista en el mensaje de cierre de F5).
- **Tanda final, F5:** botín, los 8 huevos, sonidos, arrojar murky_pearl/down_feather/solar_shard (también con dispensador), vitric_arrow con arco, solar_shard como combustible (12 800 ticks), eep con mechero, redstone y fuego.
- **Compat. ectoplasma:** con Goety instalado, las recetas de pergaminos y antorchas aceptan `goety:ectoplasm`.
- **Tanda final, F3:** botín de las 5 criaturas (con y sin Botín III), los 5 huevos (pestaña y colores), sonidos y el resto de los detalles de la lista de F3 (cerebro del minds con flecha, rebote de daño del sinister, flechas desviadas por el beauty, detritus dañado por curación instantánea, etc.).
- **Tanda final, F4:** botín (blitz_rod, sleeping_powder, flintlock, coat_scraps, ectoplasm, fiendish_essence, vertigo_chunk…), los 9 huevos, sonidos, el flintlock en la pestaña Combate (dispara y se rompe a los 4 usos), el disco NaN cuando un fiend mata a otro fiend y el disco "nothing" cuando un esqueleto mata a un nothing, el jinete esqueleto del fiend (1 de 12 al aparecer naturalmente, F8), la variante "deep" del fend (aparece bajo y=20).

## F5 - Criaturas lote 3 (2026-11-02) ✅
Lote: **minedflayer, dreamweaver, cannonball_crab** (huevo `runaway_spawn_egg`), **mockingbird, pathogen, blossom, saint_solis, stained_glass** + entidades **spat_block** (lo escupe el blossom), **murky_pearl, feather** (las tira el mockingbird), **vitric_arrow, star** (las tira el saint solis), **eep** (TNT de sueño: bloque `eep` + entidad) y los ítems arrojables de esas entidades: **murky_pearl, vitric_arrow, down_feather, solar_shard** (este último adelantado de F6: solo arroja stars; también es combustible).
- Revisado en el original el renderizado especial: dreamweaver (capas de seda con el shader `silk`, ojos, y dibuja dentro la criatura que va a invocar), saint_solis (sol de icosfera, `misc/IcoSphere` copiado tal cual), blossom (pétalos y nube de esporas con el shader `blossom`), mockingbird (alas de cintas; texturas alternativas con `remodelmb`), stained_glass y feather/vitric_arrow (estelas `ambush`), cannonball_crab (patas de cintas), pathogen (sombra que se desvanece), spat_block (bloque girando), eep (TNT que gira). Ninguno tiene manejadores de color.
- Cambios con `// PORT:` (PORT_MAP §6c). Saint solis y star: hasta F7 toman el camino `fixsas` del original (como el friend). Pathogen: se quita el import de `ToadstoolRenderPayload` (Sable, D2).
- Esperan su ítem de F6: `bacterium_ball` (botín del pathogen), `living_glass_shards` (botín del stained glass y receta de la vitric_arrow: hasta F6 las flechas vítreas solo salen del modo creativo). El minedflayer usa `PlayerBrainrotAccessor` (F6).
- Encontrado por los GameTests: el dreamweaver no se podía crear (`ConcurrentModificationException` al quitar metas mientras recorría el conjunto); la loot table del mockingbird no cargaba (condición de 1.21, ahora la convierte el script); nombres de GameTests repetidos entre clases (ahora con prefijo de fase). Bug del original que se conserva: la murky pearl disparada por dispensador tiene el tipo `snowball`.
- GameTests (`F5GameTests`, 6): los 8 huevos; botín de 7 criaturas (incluye el murky_pearl del nothing, que esperaba desde F4); gravedades (minedflayer 0, pathogen y stained_glass 0,01, la vitric_arrow cae con la gravedad variable del original); los 3 arrojables (1, 3 y 8 proyectiles) y el ítem flecha; combustible del solar_shard; el dispensador dispara la murky pearl; el bloque eep se enciende con mechero y su explosión da sleepy.

Verificado: `build` OK; `runGameTestServer` 36/36; `runServer` hasta `Done (2.772s)`; `runClientRenderSmoke` OK con 22 criaturas (sin errores del mod; capturas revisadas).

## Prueba corta de F4 + arreglos (2026-11-02)
El usuario probó el jar real: fiend, fend, blitz, eeper, nothing, canary, vertigo y el flintlock funcionan. Dos errores:
- **Coat of arms crasheaba al dibujarse** ("BufferBuilder not started", `CoatOfArmsEntityRenderer.vertex` línea 188 desde `render` línea 115). No era un buffer propio sin iniciar: el renderer pedía el consumer de la capa al buffer normal, dibujaba los flintlocks con `itemRenderer` (otro render type, que en 1.20.1 cierra el lote anterior) y después escribía en el consumer viejo. Arreglo: pedirlo después de los flintlocks. Revisados los demás renderers: ninguno lo tiene.
- **Friend invisible:** fuera de Fabulous el original dibuja el friend en un buffer propio que solo muestra la post chain "friend" (F7), y ese buffer nunca se vaciaba. Hasta F7 toma siempre el camino Fabulous del original (`// PORT: TODO F7`). `image_friend` es el nombre del friend en `en_us.json` (sale p. ej. en los mensajes de muerte); no es un problema.
- Nueva prueba automática de dibujo `runClientRenderSmoke` (PORT_MAP §6d): sin el arreglo reproduce el mismo crash (mismas líneas 188/115); con el arreglo, 14 criaturas dibujadas 30 s sin errores, y en las capturas se ve el friend.

Compatibilidad nueva (agregado del port, PORT_MAP §6d): tag `forge:ectoplasm` (este mod + `goety:ectoplasm` opcional) y las 5 recetas con ectoplasma lo piden por el tag. GameTest `CompatGameTests` (sin Goety: el tag carga con un solo ítem y las 5 recetas lo usan). Pendiente: decisión del usuario sobre cómo hacer que Goety acepte el ectoplasma de este mod.

Verificado: `build` OK; `runGameTestServer` 30/30; `runServer` hasta `Done (2.844s)`; `runClientRenderSmoke` OK (cliente hasta el mundo, sin errores del mod).

## F4 - Criaturas lote 2 (2026-11-02) ✅
Lote: **fiend, fend, friend, blitz, eeper, nothing, canary** (+ `canary_part`, los segmentos del cuerpo), **vertigo, coat_of_arms** + la entidad **bullet** (disparo instantáneo de fiend, coat_of_arms y flintlock).
- Adelantados: **kicked_block** (de F5; el fiend patea el bloque que pisa) y el ítem **flintlock** (de F6; fiend y coat_of_arms lo dibujan en la mano y el fiend suelta sus partículas). Pasan a F5: **feather** (mockingbird) y **star** (saint_solis), que el plan tenía en F4.
- Revisado en el original el renderizado especial de cada una, todo portado: fiend (flintlock flotante al disparar + capa blanca brillante `FiendCritLayer` con el shader silk al patear o disparar), fend (shader `fend` ondulado con color según el arma, textura "deep", capa de armadura, buffer propio), friend (cubo giratorio con shader `friend` + carita hacia la cámara, buffer propio, texturas alternativas con `remodelfriend`), blitz (estela de 5 copias), eeper (se hincha y parpadea en blanco antes de explotar), nothing (shader `ambush` casi invisible que se revela al golpearlo o al cargar + ojos `NoThingEyesLayer`), canary (cinta de humo `ambush` detrás + segmentos), vertigo (abanico `outlined` + `ambush` al cargar el grito), coat_of_arms (capa de cintas que se abre y muestra flintlocks), bullet (trazo amarillo), kicked_block (bloque girando con el ícono de crítico). Ninguna tiene manejadores de color.
- Cambios con `// PORT:` (PORT_MAP §6c): gravedades y altura de paso por atributos; `LivingEntitySwingMixin` (nuevo, el canary balancea el doble de lento: en 1.20.1 el método es privado); `stopInPlace`, `noBlockCollision`, `customArrow`/`getMobArrow`, `isSpawner`, `isEmpty`, paquete de alta del bullet; `BREEZE_*` → `BLAZE_*`/`PLAYER_ATTACK_SWEEP`; `DUST_PILLAR` → `BLOCK` (D3); `triggerOnDeathMobEffects` sin equivalente necesario.
- Pendiente de otras fases (a propósito): botín de cuernos del vertigo (`// PORT: TODO F6`, vuelve con `VertigoHornItem`); `murky_pearl` en el botín del nothing (F5); el daño diferido del canary ("gas leak") y la rabia del vertigo sobre el jugador dependen de `PlayerMixin` (F6), y sus efectos de pantalla de F7: hasta entonces los golpes pegan normal pero sin ese extra. El canary conserva los `System.out.println` de depuración del original.
- Problema encontrado: el flintlock con `durability(4).stacksTo(1)` impedía cargar el mod ("Unable to have damage AND stack"), y `runGameTestServer` igual terminaba en BUILD SUCCESSFUL. Corregido y anotado en CLAUDE.md: el resultado vale por la línea "All N required tests passed", no por Gradle.
- GameTests (`F4GameTests`, 8): los 9 huevos; botín de 7 criaturas; gravedades, altura de paso y altura de ojos; el canary balancea el doble que un zombi (mixin); el eeper explota cerca de su objetivo y lo deja con sleepy y dañado; el bullet daña lo que tiene enfrente; el kicked_block pega 5 y se rompe; el friend se aplana al recibir un golpe y no recibe daño aplanado; el fend aparece armado y con casco.

Verificado: `build` OK; `runGameTestServer` 29/29; `runServer` hasta `Done (4.240s)` (los 2 mixins se aplican) sin errores del mod; `runClient` hasta la pantalla de título sin errores del mod (cerrado por su PID).

## Prueba corta en el juego de F3 + plumas (2026-11-02) ✅
El usuario probó el jar real (`C:\cf\test`): las 5 criaturas se ven y se animan bien y lo característico de cada una funciona; las plumas cambian de color y el pincel las alterna. `latest.log` sin errores del mod: solo los avisos conocidos y "Object did not get ID it asked for" de los 5 huevos (el mundo de prueba se creó con el jar de F2 y Forge renumera los ítems nuevos; es normal).

## F3 - Criaturas lote 1 (2026-11-02) ✅
Lote elegido por depender menos de fases futuras: **minds** (+ `MindsEntityPart`, el cerebro que se puede golpear aparte), **machination**, **sinister**, **beauty**, **detritus**. Usan solo partículas, sonidos, red y un shader core ya portados en F1–F2.
- Pasan a otras fases: **coat_of_arms → F4** (dispara la entidad `bullet`) y **blossom → F5** (escupe `spat_block`). Quedan: F4 = fiend, fend, friend, blitz, eeper, nothing, canary, vertigo, coat_of_arms; F5 = dreamweaver, cannonball_crab, minedflayer, mockingbird, pathogen, saint_solis, stained_glass, blossom.

Hecho:
- `init/CFEntityInit` (5 tipos, mismos tamaños y rangos), las 5 entidades portadas, atributos (`CFEvents.ModBus.registerAttributes`), spawn placements (sin spawn natural hasta F8), `hurtMinds` (golpear el cerebro le pega al minds), 5 huevos `ForgeSpawnEggItem` con los colores del original en la pestaña de huevos.
- Cliente: 5 modelos y 5 renderers con la API de vértices de 1.20.1, capas registradas (incluida la armadura del minds). Revisado en el original qué tiene cada uno: minds (ojo flotante cuando pierde el cerebro + capa de armadura + se pone más rosado (menos verde) mientras corre después de un golpe), machination (señal giratoria cuando carga el salto), beauty (anillo de pétalos al girar), detritus (shader `monochrome` con su propio buffer en `CFClientProxy`, se desatura con cada copia), sinister (sin extras). Ninguno tiene manejadores de color.
- Loot: el script convierte las loot tables de entidades (funciones y condiciones de looting de 1.21 → 1.20.1, PORT_MAP §6b). Beauty no suelta `bouquet` hasta F6.
- Cambios con `// PORT:` (resumen en PORT_MAP §6c): alturas de ojos/montura como overrides; gravedad del beauty con el atributo `ENTITY_GRAVITY`; `SinisterMeleeGoal` (en 1.20.1 `MeleeAttackGoal` no mira `isWithinMeleeAttackRange`, y sin esto el sinister pegaría cuerpo a cuerpo, cosa que el original no hace); detritus no-muerto con `getMobType()` (el tag `minecraft:zombies` no existe en 1.20.1); `BREEZE_WHIRL` → `PLAYER_ATTACK_SWEEP` y `DUST_PILLAR` → `BLOCK` (D3).
- GameTests (`F3GameTests`, 7): los 5 huevos sacan su criatura; las loot tables sueltan los ítems del original (300 tiradas con jugador asesino); golpear el cerebro del minds lo deja sin cerebro y sin partes; el espejo del sinister bloquea los golpes de frente y no los de atrás, y usa `SinisterMeleeGoal`; el beauty esquiva el primer golpe, flota mientras gira (gravedad negativa) y vuelve a 0,056; el detritus es no-muerto y se divide 3 veces al bajar de 12 a 8 de vida; atributos y altura de ojos del original.

Verificado: `build` OK; `runGameTestServer` 21/21; `runServer` hasta `Done (4.367s)` sin errores del mod; `runClient` hasta la pantalla de título sin errores del mod (ahí se construyen los renderers y se hornean los modelos). El dibujo de las criaturas en el mundo no lo cubre ninguna prueba automática: se ve en las pruebas en el juego.

## Pruebas en el juego de F1 + F2 (2026-11-02) ✅
El usuario probó el jar real en una instancia aparte (`C:\cf\test`): pestañas, carapacho, gelatina, sleepy, fiendish, comidas, partículas (sleepy, glass_shard, crit_text, spore), discos, recetas, drops y adornos funcionaron. En el `latest.log` de la prueba no hay errores del mod: solo los avisos conocidos (uniforms de shaders, sonido `entity.stained_glass_retreat`) y la corrección de los `.toml` de configuración creados por primera vez.

Falla encontrada y corregida:
- **El pincel no cambiaba las plumas a mosaico** (ni al bloque ni a la alfombra), aunque salían las partículas. Causa: el cambio de bloque sí se hacía en el servidor, pero las plumas normales y las mosaico usan **el mismo modelo** (igual que en el original) y solo se distinguen por el color (`tintindex`) que ponen los manejadores de color del cliente. Esos manejadores (`registerBlockColors`/`registerItemColors` del original) no se habían portado en F1, así que las cuatro variantes se veían iguales, sin teñir.
- Arreglo: `blocks/FeatherTints` (nuevo, `// PORT:`) tiene los colores y fórmulas del original sin clases de cliente (`java.awt.Color` → ARGB); `CreatureFeatureClient` registra los manejadores de bloque e ítem para las 4 variantes, como el original.
- GameTests (`FeathersBrushGameTests`, 2): `brushTogglesMosaicFeathers` usa un pincel real por el mismo camino que un clic derecho en el servidor (`ServerPlayerGameMode.useItemOn` con un `FakePlayer`) sobre el bloque y la alfombra, y comprueba que pasan a mosaico y vuelven. **Pasó también antes del arreglo** (eso confirmó que el servidor estaba bien). `mosaicAndNormalFeathersHaveDifferentTints` comprueba que los tintes de mosaico y normal son distintos, que ninguno queda blanco y que coinciden con los valores del original; antes del arreglo no existía `FeatherTints`. Los manejadores de color en sí son de cliente y no se pueden ejecutar en el servidor de GameTests: se verificaron con `runClient`.

Verificado: `build` OK; `runGameTestServer` 14/14; `runServer` hasta `Done (3.543s)`; `runClient` hasta la pantalla de título sin errores del mod; jar `build\libs\creaturefeature-1.2.3.3-port.0-all.jar` con el arreglo.

## Decisiones del usuario (2026-11-02)
- **D1:** base = código fuente de GitHub. F1–F8 portan el contenido publicado en el jar 1.2.3.3. El contenido ★ queda para **F9** y se decide ahí; la idea del usuario es portar **solo el Powder Skull** (con su botín pasado a las **mazmorras**) y dejar afuera **Trichael, Fish Eye y Wraith** por estar inacabados.
- **D2:** se quitan Toadstool y runic_stone_bricks (Sable). Documentado en PORT_MAP §6.
- **D3:** sin INFESTED/OOZING en blighted_brain; sonidos de 1.20.1 parecidos en lugar de los de breeze; partícula genérica de bloque/ítem en lugar de DUST_PILLAR/ITEM_COBWEB. Cada cambio con `// PORT:`.
- **D4:** no se incluye Galena Hats.
- Cada fase se cierra con `runServer` hasta `Done`.

## F2 - Red, NBT, partículas, shaders core, discos (2026-11-02) ✅
Hecho:
- **Red:** `networking/CFNetwork` (`SimpleChannel` "creaturefeature:main", protocolo "1") con los 8 paquetes del original (S→C: VelSync, DeAmp, Rabies, Pathogen, Squashed, GasLeak; C→S: C2SVelSync, C2SMan), mismos campos y tipos. Ayudantes `sendToPlayersTrackingChunk/Entity` y `sendToServer` como los de NeoForge. Lo de cliente está en `client/CFClientPayloadHandler`, llamado por `DistExecutor`. Validaciones nuevas (`// PORT:`): C2SVelSync ignora empujes > 1,7 o no finitos (el cuerno manda 1,66); C2SMan solo afecta al que lo manda.
- **Accessors** `LivingEntityGoopAccessor` y `PlayerBrainrotAccessor` (interfaces). Los implementan los mixins de F6: hasta entonces los paquetes que los usan no hacen nada.
- **NBT en lugar de componentes:** `init/CFDataComponentTypeInit` guarda `opened_mind` (int) y `dreamcatcher_contents` (lista de stacks) en el NBT del ítem con los mismos ids; `items/DreamCatcherContents` con `save/load`. Los usan open_mind y dream_catcher en F6.
- **Partículas:** 17 tipos con dibujo (`CreatureFeatureClient.registerParticleProviders`), 18 clases portadas (`RevealParticle` no se registra, como en el original), JSON y texturas. `CFTextureSheetParticle` (nuevo) recrea `renderRotatedQuad` de 1.21; `Math.clamp` (Java 21) → `Mth.clamp`.
- **Shaders core:** `client/CFRenderTypes` con 9 render types y 9 shaders (los 5 ★ esperan a F9) + `CFSheets`. Los 3 vertex shaders propios adaptados al marco de coordenadas de 1.20.1 (PORT_MAP §6b).
- **Discos:** `music_disc_nothing` (`RecordItem`, comparador 15, 16,3 s) y `music_disc_nan` (`NanDiscItem`, comparador 3, 71 s) con el tag `#minecraft:music_discs` y la descripción del tooltip. Pestaña Herramientas, junto a los discos vanilla. El título animado del NaN se dibuja en F7 (CFGuiMixin); en F2 solo se arranca.
- `tools/convert_resources.py`: copia partículas y shaders core; convierte loaders de modelo `neoforge:` → `forge:`; agrega las claves `.desc` de los discos; no pisa los archivos editados a mano (`HAND_EDITED`).
- GameTests (`F2GameTests`, 6): ida y vuelta de los 8 paquetes; validación del empuje; NBT del dream catcher y opened_mind (guardando y cargando el stack); 17 tipos de partícula; datos de los discos; los 2 discos entran en un tocadiscos real.

Problemas encontrados y resueltos en la fase:
- El tocadiscos de 1.20.1 rechazaba los discos (y el disco se perdía) sin el tag `#minecraft:music_discs` → tag agregado (lo detectó el GameTest).
- `runClient` crasheaba al compilar `rendertype_entity_fend` (`fog_distance` de 1.21) → shaders adaptados. `particles/reveal.json` sin tipo registrado y el loader `neoforge:separate_transforms` también daban error en el cliente → corregidos.

Verificado: `build` OK; `runGameTestServer` 12/12; `runServer` hasta `Done (2.958s)` sin errores del mod; `runClient` hasta la pantalla de título sin errores del mod (9 shaders compilados, atlas de partículas creado). Avisos que quedan y son normales: "could not find uniform …" de los shaders (uniforms declarados que el GLSL no usa; igual que vanilla) y "Missing sound for event: creaturefeature:entity.stained_glass_retreat" (el original registra ese sonido sin entrada en `sounds.json`).

## F1 - Bloques, ítems simples, sonidos, efectos, recetas, adornos (2026-11-02) ✅
Hecho:
- **Bloques (17):** minedflayer_jelly, carapace_block, carapace_bricks, carapace_brick_stairs, down_feathers, down_feathers_carpet, mosaic_down_feathers, mosaic_down_feathers_carpet, fiendish_tiles, blind_fiendish_tiles, murky_pearl_tiles, murky_pearl_tile_stairs, murky_pearl_block, dream_silk_spool, wallpaper, solar_block, solar_bricks (+ sus ítems, modelos, loot tables y tags de herramienta).
- **Ítems simples (15):** blighted_brain, vertigo_chunk, sleeping_powder, coat_scraps, fiendish_essence, carapace, thingamabob, blitz_rod, dream_silk, ectoplasm, scroll_pan/trans/pride/bi, minedflayer_goop.
- **Sonidos (12)** con sus `.ogg`, **efectos** fiendish y sleepy (con ícono), **tipos de partícula** (17, sin dibujo hasta F2), **8 triggers de logros** (`CriteriaTriggers.register`), **config** (`ForgeConfigSpec`, mismas claves).
- **Recetas (28)** convertidas a 1.20.1, **5 materiales de adorno** + atlas de adornos con el tipo vanilla, tag `blueprint:generates_overrides`, idioma (en_us, ru_ru), texturas y modelos.
- Pestaña creativa: los ítems de F1 en los mismos lugares que el original (blitz_rod después de la vara de blaze, porque no hay vara de breeze).
- `SleepyJumpMixin` (nuevo, solo del port): sleepy anula el salto (en 1.20.1 no hay atributo de salto).
- Resto del cambio con `// PORT:` en el código. `tools/convert_resources.py` copia y convierte recursos; se vuelve a correr en cada fase (solo escribe lo que ya está registrado y lista lo saltado).
- GameTests (`F1GameTests`): bloques con ítem y loot table; recetas (incluye resultado de stonecutting); materiales de adorno en el registro y en `#minecraft:trim_materials`; sleepy baja la velocidad a la mitad; sleepy impide saltar (con un zombi de control que sí salta).

Pendiente de otras fases (dejado a propósito):
- `// PORT: TODO F6` en CarapaceBlock (efecto "aplastado") y MinedFlayerJellyBlock (goop): dependen de `LivingEntityGoopMixin` + red.
- Bloques eep (F5) y doohickey (F6). Recetas saltadas por ítems de fases futuras: alt_down_feather, bacterium_ball, blaze_powder, doohickey, down_feathers, dreamcatcher, eep, fiendish_soda, flintlock, flower_clone (serializer especial), glass, heavy_axe, murky_pearl_block, open_mind, solar_block, the_pill, tinted_glass, vertigo_horn, vitric_arrow. Tags: doohickey en `mineable/pickaxe`, vitric_arrow en `arrows`, heavy_axe en `axes`. Se agregan solos al volver a correr el script.
- Sin Blueprint, los ítems de armadura adornados con estos materiales muestran el icono sin adorno (el adorno sobre el cuerpo sí se ve). Igual que el original sin Blueprint.

Verificado: `build` OK, `runGameTestServer` 6/6 (5 de F1 + 1 de F0), `runServer` hasta `Done (2.773s)` sin errores ni avisos del mod en el log (EULA aceptado por el usuario en `run\eula.txt`; el servidor se cerró por su PID).

## F0 - Análisis y esqueleto (2026-11-02)
- Fuente vs jar: la fuente de GitHub es **más nueva** que el jar 1.2.3.3 (23 clases y 47 recursos más, 1 mixin más). Detalle en PORT_MAP §1.
- Inventario, mixins, diferencias 1.21.1→1.20.1, dependencias y plan: PORT_MAP §2–§8.
- Esqueleto Forge 1.20.1 (Forge 47.4.10, Gradle 8.8, ForgeGradle 6, mapeos oficiales):
  - Java 17 por toolchain: daemon fijado con `gradle/gradle-daemon-jvm.properties` y compilación con `java.toolchain` 17 + foojay. Verificado: con Java 25 en el `PATH` y sin `JAVA_HOME`, el build imprime `Java: 17.0.20.1`.
  - MixinExtras 0.4.1 por jarJar (`build\libs\creaturefeature-<v>-all.jar` es el jar para el juego).
  - `src/gametest` con `SkeletonGameTests.modIsLoaded` y la estructura `empty9x5x9`.
- Verificado: `gradlew.bat build` OK; `gradlew.bat runGameTestServer` → "All 1 required tests passed".
