# PROGRESS - Creature Feature port a Forge 1.20.1

## Estado actual
**Port completo y probado en el juego (2026-11-03); ajustes del 2026-11-09 (efectos del cerebro) sin probar todavía en el juego.** F0-F10 cerradas, versión `1.2.3.3-forge.1` (etiqueta `port-completo`). Tanda final hecha con todo el modpack; lo que falta se ve jugando (abajo).

## Por ver jugando (sin prueba dedicada)
Lo que queda de la tanda final; el usuario lo va viendo en partidas normales. La lista detallada con comandos está en el historial (commit `6a79c05`, "Pruebas en el juego pendientes").
- **Apariciones naturales** de todas las criaturas en sus biomas (con la versión personal: vertigo en biomas fríos, stained glass en el End, blossom en las cuevas frondosas, beauty/nothing/mockingbird nunca, zombiflore sin lush_caves). **Blossom en lush_caves sin confirmar:** como todo monstruo necesita luz de bloque 0, y en las cuevas frondosas del modpack hay mucha luz (bayas brillantes); las listas de aparición sí están verificadas (`CF_PERSONAL_OK`).
- **Logros** que faltan: horse, friendless, sinister, gas, renovation.
- **Discos:** NaN (un fiend mata a otro fiend) y "nothing" (un esqueleto mata a un nothing).
- **Botín** con y sin Botín III, huevos y sonidos de cada criatura, variantes (fend "deep" bajo y=20, fiend con esqueleto montado).
- **Interruptor por criatura** en un mundo normal (`beauty = false`: el que existía desaparece; `/summon` sin NBT funciona, con NBT no).
- **Compat Oreganized / Caverns and Chasms** (drops extra), si se instalan completos.

## Ajustes posteriores (2026-11-09)
- **Efectos del cerebro marchito** (decisión del usuario, "Trials o propios"): vuelven Infested y Oozing. Con Trials Chambers instalado, los suyos (`trials:infested`, `trials:oozing`); sin él, copias propias del comportamiento de 1.21 (`creaturefeature:infested`, `creaturefeature:oozing`). Trials Chambers es opcional: sin él no hay errores. `EffectsGameTests` (3): qué efectos da el cerebro según esté o no Trials, los pececillos de Infested y los slimes de Oozing.
- **Fuera definitivamente:** Toadstool y Runic Stone Bricks (Sable solo existe para 1.21.1) y Galena Hats (ni como dependencia). Documentado en CHANGES.md, README y PORT_MAP (D4, D5).
- **CHANGES.md** completado con lo que faltaba: botín del detritus con Botín I/II, `pathogen = true`, adornos sin Blueprint, fend y eeper.
- **Rutas:** el proyecto pasó a `C:\Projects\cf\` (script de recursos, CLAUDE.md, PORT_MAP, PROGRESS y `PersonalPackCheck`).

Verificado: `build` OK; `runGameTestServer` 62/62 **sin** Trials (el cerebro da `creaturefeature:infested`/`oozing`) y 62/62 **con** Trials (`-PwithTrials`: `trials:infested`/`oozing`); `runServer` hasta `Done` en los dos casos; `runClientRenderSmoke` OK, con los íconos y nombres de los efectos propios en el inventario (`cf_effect_icons.png`).
Por probar en el juego: comer el cerebro marchito con y sin Trials Chambers en la instancia de prueba.

## Tanda final en el juego (2026-11-03) ✅
El usuario probó el jar real `1.2.3.3-forge.1` en una copia de su mundo con **todo el modpack** (371 mods, Oculus 1.8.0 con el shaderpack Complementary Unbound activo), con `cf_personal` en la carpeta `datapacks` del mundo y la configuración personal en `serverconfig`. Funcionó:
- arranque, versión y créditos;
- ítems y recetas: flintlock, dispensador con la murky pearl, eep, open_mind y heavy_axe;
- los efectos que faltaban: rabia, minedflayer, adornos y caparazón que cae;
- la versión personal: `cf_personal` activo y el beauty desactivado aparece con `/summon`;
- la conversión de ectoplasma con Goety.

Log (`%APPDATA%\.minecraft\logs`: `2026-11-02-1.log.gz` 23:47-23:59, rotado a medianoche, y `latest.log` 00:00-00:09): **ningún error de Creature Feature ni de `cf_personal`, ningún crash report, ningún conflicto de mixins**. Avisos del mod, todos conocidos y sin efecto:
- `could not find sampler/uniform` de los shaders del mod (datos del original que el compilador GLSL descarta);
- `Missing sound ... stained_glass_retreat` (error del original);
- `Object did not get ID it asked for` (la copia del mundo ya tenía el mod con otra numeración; Forge reasigna los IDs);
- `creaturefeature-client.toml is not correct. Correcting` (primera carga del mod en esa instancia: Forge completa el archivo).
- 23 líneas en STDOUT de `IcoSphere.MakeIcosphere`: impresiones de depuración que ya trae el original, inofensivas.

Errores del log que mencionan mods de la compat pero **no son del port**: 19 recetas de Dixta's Armory y de Oreganized que piden `oreganized:electrum_upgrade_smithing_template` (ese ítem no existe en el modpack) y la tabla `caverns_and_chasms:blocks/eumus_brick` del propio Caverns and Chasms.

## F10 - Opción por criatura, versión personal y publicación (2026-11-02) ✅
- **Opción por criatura** (agregado del port, va en el mod publicado): sección `[creatures]` en `creaturefeature-server.toml`, una clave por criatura (22), todas `true`. Una desactivada no aparece de forma natural, no la agrega ningún spawner ni otra criatura del mod, y si ya existe desaparece sin botín (al cargar o en su siguiente tick). Huevos, dispensador con huevo y `/summon` sin NBT siguen funcionando para los admins (marcan la entidad con el tag `creaturefeature_allowed`; con NBT hay que agregarlo a mano). Con todo activado no cambia nada (el conjunto de desactivadas queda vacío y los manejadores salen enseguida). `misc/CreatureToggles`.
- **Versión personal** (fuera del repositorio y del jar): `C:\Projects\cf\personal` con el datapack `cf_personal`, `creaturefeature-server.toml` y `LEEME.md`. Se instala en `global_packs
equired_data` (Global Packs, ya en el modpack). Verificado con `runServer -Dcreaturefeature.personalCheck=true` (`PersonalPackCheck`, solo desarrollo): carga sin errores y las listas de aparición quedan como se pidió (`CF_PERSONAL_OK`). Forge 1.20.1 respeta `forge:conditions` en los biome modifiers: el archivo de Zombiflore se salta sin error si falta Skarrier Mobs.
- **Publicación:** versión `1.2.3.3-forge.1`; créditos a AtiredDev y Team Galena en `mods.toml` (`authors`, `credits`, `displayURL`) y en `README.md`; `LICENSE` (MIT); `CHANGES.md` con lo quitado, adaptado, corregido y agregado. Los tres van dentro del jar (`META-INF`).
- GameTests (`F10GameTests`, lote propio `f10_toggles`, 3): una criatura desactivada no entra al mundo, la que ya existía desaparece sin botín y el huevo sigue andando; una activada aparece igual que siempre y sin marca; las 22 claves existen y valen `true` por defecto.

Verificado: `build` OK; `runGameTestServer` 59/59; `runServer` hasta `Done (2.611s)`; `runClientRenderSmoke` OK (criaturas, efectos en los 3 modos y pantalla de logros, sin crash reports).

## F9 - Contenido ★ (2026-11-02) ✅ (vacía)
Se revisó la Powder Skull en la fuente: no ataca, no tiene sonidos ni botín propios, no aparece sola (solo del spawner ominoso de 1.21) y su huevo no tiene pestaña ni traducción. Decisión del usuario: queda fuera, igual que Trichael, Fish Eye y Wraith (PORT_MAP, "Contenido quitado").

## Prueba corta de F8 (2026-11-02) ✅
El usuario probó el jar real: árbol de logros con el fondo animado, logros sleepy y pill, el efecto "man" (y roots_man), fiends en nether_wastes, cangrejos en la playa y el cangrejo bajo el agua sin ahogarse. Confirmó que la prueba de F7 fue en Elegantes.
Log (`C:\Projects\cf\test\logs\latest.log`, 22:48-22:51, solo la última sesión) sin errores del mod ni crash reports nuevos. Avisos: los ya conocidos de F7 (shaders, `stained_glass_retreat`) y 3 `Received passengers for unknown entity` al llegar a nether_wastes: los fiends que aparecen con un esqueleto montado (1 de cada 12) se arman igual que la araña jinete vanilla (`startRiding` en `finalizeSpawn`, mismo código que el original) y vanilla da el mismo aviso; sin efecto.

## F8 - Apariciones, compatibilidades por datos y logros (2026-11-02) ✅
- **Apariciones naturales:** 26 biome modifiers del original (23 `add_spawns` + 3 `remove_spawns`) con los mismos biomas, pesos y tamaños de grupo; `toadstool` fuera (D2). Los tags `c:` se traducen según PORT_MAP §4. Las 21 criaturas ya tenían su `SpawnPlacement` igual que el original.
- **Compatibilidades por datos:**
  - Oreganized (machination → pepita de electrum; minds → pepita de plomo, 75 %; sinister → lingote de plata, 75 %) y Caverns and Chasms (sinister → lingote de plata, 75 %), todo con 0-2 + Botín. Loot modifier propio `creaturefeature:add_table` (Forge 1.20.1 no lo trae). Los ítems de los otros mods van por tags opcionales: sin esos mods no cae nada y no hay errores de carga.
  - Blueprint (`blueprint:generates_overrides` de los adornos): ya estaba desde F4; confirmado que Blueprint 1.20.1 (7.1.4) usa ese tag.
  - `ominous_powder_skull` no se porta (trial chambers, 1.21).
- **Logros:** los 9 del original (root, sleepy, pill, horse, friendless, sinister, gas, renovation, roots_man con su huevo de recompensa `one_egg`). Los triggers ya estaban desde F1-F6. Mixins de pestaña: `AdvancementTabMixin` (fondo animado de 64 px) y `AdvancementWidgetMixin` (el logro del "man": arranca la búsqueda al pasar el mouse).
- **Pendiente olvidado de F5, resuelto:** el cangrejo respira bajo el agua y es acuático (los tags de entidad de 1.21 no existen en 1.20.1).
- GameTests (`F8GameTests`, 6): biomas de cada tipo de traducción con peso y grupo; nada en mushroom_fields/deep_dark ni dreamweaver en la isla principal; los 26 modifiers cargan; los 9 logros con su árbol y triggers; `add_table` agrega la tabla; las tablas de compat cargan y los tags quedan vacíos sin los mods; el cangrejo.
- Prueba de dibujo: al final abre la pantalla de logros en la pestaña del mod (`cf_advancements.png`).

Verificado: `build` OK; `runGameTestServer` 56/56 sin errores de datos; `runServer` hasta `Done (2.943s)`; `runClientRenderSmoke` OK (criaturas, efectos en 3 modos y pantalla de logros).

## Prueba corta de F7 (2026-11-02) ✅
El usuario probó el jar real: sueño, fiend, gas del canary, bacterias, título del disco NaN, friend y saint solis, gelatina y cambio de tamaño de ventana funcionan. El mensaje dejó sin completar el modo gráfico; `C:\Projects\cf\test\options.txt` no cambió desde las 15:24 y dice `graphicsMode:1`, o sea **Elegantes** (cambiar el modo desde el menú reescribe ese archivo). Rápidos y Fabulosos van a la tanda final.
Log (`C:\Projects\cf\test\logs\latest.log`, 22:21-22:29) sin errores del mod ni crash reports. Avisos esperables:
- `could not find sampler/uniform ...` de los shaders del mod (33): son samplers y uniforms declarados en los JSON que el compilador GLSL elimina por no usarse; los JSON son los del original, sin efecto visible.
- `Missing sound for event: creaturefeature:entity.stained_glass_retreat`: el original registra ese evento pero su `sounds.json` no lo define (error del original, sin efecto).
- `Object did not get ID it asked for`: el mundo de prueba se creó con una versión anterior del port (con menos ítems); Forge reasigna los IDs numéricos, es normal.

## F7 - Efectos de pantalla y mixins de cliente (2026-11-02) ✅
- **Post chains (9):** minedflayer, man, sleep, rabies, haze, bacte, friend, fiend, sun, con sus targets (haze, friendlytarget, suntarget, suntarget2, rabiestargeteheheh). Se crean en `RegisterShadersEvent` (bus del mod). El script de recursos ya copia `shaders/post` y `shaders/program`; los nombres con namespace (`creaturefeature:sleep`) cargan bien en Forge 1.20.1.
- **Eventos de cliente del bus del juego** en `CreatureFeatureClient.ForgeBus`: aplastado y estela de caída (`RenderLivingEvent.Pre`), anillos del pathogen y charco de goop (`Post`), volcado de los buffers del friend y del sol en sus targets (`RenderLevelStageEvent` AFTER_ENTITIES), tick de cliente (tamaño de ventana, sampler del haze, desvanecido de los valores del `PROXY`).
- **Mixins de cliente (10):** CFGameRendererMixin, CFPostChainMixin, CameraMixin, CameraTypeMixin, CFGuiMixin, GuiGraphicsMixin, FallingBlockEntityRendererMixin, HumanoidArmorLayerMixin, IamActivelyHavingaMigraineMixin, LivingEntityRendererMixin; 3 accesores nuevos. AdvancementTabMixin y AdvancementWidgetMixin pasan a F8 con los logros.
- **Friend y sol vuelven al camino del original:** el friend fuera de Fabulosos y el saint solis / star sin `fixsas` se dibujan en su buffer propio y los muestra su post chain (`getSunSource` nuevo).
- Diferencias (PORT_MAP §3 y §6c): `PostChain.setUniform` → `CreatureFeatureClient.setUniform`; `ClientTickEvent.Pre` → `TickEvent` START; `java.awt.Color` → `Mth.hsvToRgb`; tinte del pathogen con `@WrapOperation` sobre los 4 floats de `renderToBuffer`; `HAZE_TARGET` se reinicia al recargar recursos (el original quedaba con la cadena vieja).
- **Prueba de dibujo ampliada:** después de las criaturas, recorre Rápidos, Elegantes y Fabulosos y en cada modo fuerza ninguno, sleepy, fiendish, rabies, flayed, gas, bacte, man, nan_title y efectos de entidad (víctima con anillos, goop, aplastado y estela; armadura con los 4 adornos del mod; caparazón cayendo). Saca 30 capturas `cf_fx_<modo>_<efecto>.png`. En 1.20.1 la opción de gráficos no reconstruye nada sola (lo hace el botón del menú): la prueba llama a `levelRenderer.allChanged()`, sin eso Fabulosos crashea por falta de targets.

- **Pendientes olvidados de F6, resueltos en F7:** la gelatina del minedflayer vuelve a dar goop (al caer y al pisarla) y el caparazón que cae vuelve a aplastar (`SquashedPayload`), como en el original. Ya no quedan `TODO` de F1-F7 en el código.
- `rabies2`, `flayed2` y `fiendish2` del `PROXY` no se asignan en ningún lado, tampoco en el original: no hay nada que portar.

Verificado: `build` OK (refmap con los nombres SRG de los 10 mixins); `runGameTestServer` 50/50 (los mixins de cliente no se cargan en el servidor); `runServer` hasta `Done (2.911s)` sin errores del mod; `runClientRenderSmoke` OK en los 3 modos, sin errores de shaders, y cada efecto se ve en su captura.

## Prueba corta de F6 y arreglos de F4 (2026-11-02) ✅
El usuario probó el jar real: cuerno, atrapasueños, open_mind, ramo, píldora, soda, bola de bacterias, vidrio vivo, hacha pesada, doohickey y tijeras funcionan. Los arreglos de F4 también: el coat of arms ya no crashea (el crash de las 19:56 en `C:\Projects\cf\test\crash-reports` era del jar anterior al arreglo, mismas líneas 188/115) y el friend se ve.

## F6 - Ítems con lógica y mixins comunes (2026-11-02) ✅
- **Ítems:** vertigo_horn, dream_catcher (`SpawnerCaptureItem`), open_mind, the_pill, bouquet + receta especial `flower_clone` (`FlowerCloneRecipe`, serializer `crafting_special_flowerclone`), fiendish_soda, bacterium_ball, living_glass_shards, heavy_axe y el bloque **doohickey** (block entity + renderer con la esfera de icosfera y el ítem flotando; acepta sleeping_powder, minedflayer_goop, vertigo_horn, bacterium_ball y fiendish_essence). Item properties de cliente (`tooting`, `full`, `open`). El flintlock y el solar_shard ya estaban (F4, F5).
- **Mixins comunes (7):** `AbstractBlockMixin` (el friend aparece sobre bedrock), `AbstractContainerMenuMixin` (el crafteo del open_mind no se cierra sin mesa), `LivingEntityGoopMixin` (fiendish ×1,8 y se consume; infección "bacterial" → pathogen; caída amplificada del hacha pesada; goop/aplastado), `MonsterEntityMixin` (8 % de no-muertos infectados), `PlayerAdvancementsMixin`, `PlayerMixin` (daño diferido del canary, rabia, brainrot; la parte de cliente "man" pasó a `CFClientPlayerHooks`), `ShearsItemMixin` (velocidad 8 en `#shearable`). Los 9 mixins se aplican (los 8 primeros al arrancar, `PlayerAdvancementsMixin` al entrar un jugador).
- Vuelve el botín de cuernos del vertigo (era `TODO F6`).
- Cliente fuera del código común: el dash del cuerno (`LocalPlayer`) y el sonido/temblor del open_mind pasan a `CFClientItemHooks` por `DistExecutor` (el original los tenía en los ítems). El temblor y los efectos de pantalla (`PROXY`) se ven en F7.
- **Recetas con ítems de 1.21:** `open_mind` (`minecraft:crafter`) y `heavy_axe` (`minecraft:heavy_core`) no tienen equivalente claro. Decisión del usuario: mesa de crafteo y lingote de netherita (PORT_MAP §6c; las aplica el script). Lo detectó el log de `runServer` ("Parsing error loading recipe"); nuevo GameTest `f6EveryRecipeFileLoads` para que no se repita.
- Corregido de F5: solar_shard y murky_pearl van en la pestaña Herramientas, como en el original.
- GameTests (`F6GameTests`, 12): todas las recetas cargan; tijeras y bedrock; flower_clone (juntos sí, en diagonal no, el ramo pierde durabilidad); el dream catcher mueve un spawner de blaze con su mob; el crafteo del open_mind sigue válido sin mesa; fiendish ×1,8 y se consume; un zombi infectado suelta un pathogen; ≈ 8 % de no-muertos infectados; ticks de `PlayerMixin` (rabia y brainrot bajan); la píldora, la soda, el vidrio vivo y la bola de bacterias; el cuerno trae instrumento, enfría y da rabia; el doohickey toma el ítem y lo guarda.

Verificado: `build` OK; `runGameTestServer` 50/50; `runServer` hasta `Done (2.571s)` sin errores del mod (con las recetas de 1.21 fuera); `runClientRenderSmoke` OK (22 criaturas + doohickey; `PlayerAdvancementsMixin` aplicado al entrar).

## Prueba corta de F5, decisiones y análisis de rendimiento (2026-11-02)
- El usuario probó F5 en el jar real: todo funciona. `latest.log` sin errores del mod (solo los avisos conocidos y la renumeración de IDs de los ítems nuevos).
- Decisiones aplicadas: recetas de conversión 1:1 con Goety, solo con Goety instalado (PORT_MAP §6d; GameTest: sin Goety no se cargan y no hay errores de recetas en el log). Error del original corregido: la murky pearl del dispensador ya no es una bola de nieve (`// PORT:`, GameTest).
- **Rendimiento (solo análisis, sin cambios en el mod).** `PerfGameTests` (solo desarrollo, `-Dcreaturefeature.perf=true`): 1/10/30 de cada uno alrededor de un jugador (FakePlayer agregado al nivel), de noche, 100 ticks medidos después de 40 de asentamiento, segunda pasada (la primera calienta el JIT). Tiempo del tick del nivel, línea base vacía 4,8-5,1 ms (ruido ≈ ±0,3 ms):

  | | ×1 | ×10 | ×30 |
  |---|---|---|---|
  | zombi | 4,66 | 4,80 | 4,50 |
  | pathogen | 4,94 | 4,72 | 4,98 |
  | blossom | 4,60 | 5,48 | 5,80 (22 spat_blocks volando) |

  Zombis y pathogens: sin diferencia medible con la base. Blossom: ≈ +0,7 ms con 10 y ≈ +1 ms con 30 (≈ 30 µs cada uno), por el escupitajo cada 50 ticks cuando su objetivo está a menos de 5 bloques y los spat_blocks en vuelo (cada uno mira colisiones por tick y, al caer, busca entidades en ±4 bloques). El TimeTracker por entidad de Forge no registró nada en el servidor de GameTests; por eso se mide el tick del nivel.
  - Cliente (estimado del código): el blossom agrega ≈ 40 cuadriláteros por cuadro (pétalos 6×6 + esporas 4; un zombi son ≈ 36); cada spat_block suelta 3 partículas `spore` por tick (viven 40 ticks) y al caer 16 `spore` + 1 `spit` (60 ticks): con 30 blossoms atacando (≈ 22 en vuelo) son ≈ 2 600 partículas vivas, con 10 ≈ 850 (el límite de Minecraft es 16 384). El pathogen solo suelta `sparkle` al infectar y dibuja su modelo y 2 cuadriláteros.
  - **De dónde salen los pathogens:** no tienen aparición natural. El 8 % de los no-muertos que aparecen (`MonsterEntityMixin`, en `finalizeSpawn`; con `pathogen = true` en la config, solo en biomas templados) queda "bacterial"; cuando está en combate o herido, al bajar su contador suelta un pathogen con 7 de vida (`LivingEntityGoopMixin`). El pathogen vuela hacia monstruos heridos y al golpearlos los infecta (cadena). También infecta la `bacterium_ball`. Esos mixins e ítem llegan en F6: hasta entonces solo salen del huevo o `/summon`.
- **Zombiflore (Skarrier Mobs 1.1.2, solo lectura):** aparece en jungle, sparse_jungle y lush_caves (peso 20, 1-2 por grupo; más una condición propia con la luz de bloques); el blossom del original aparece en `#c:is_jungle` con peso 30, 1-3 (spawns naturales en F8). Es un `Monster` (no un zombi): 20 de vida, daño 3, velocidad 0,15, rango 32. Por tick, su procedimiento de MCreator lee y escribe ≈ 10 valores en el NBT persistente y hace **siempre** una búsqueda de `FloreHeadEntity` en una caja de 32 bloques; si no tiene cabezas, además busca todas las entidades en ±16 bloques y las ordena por distancia. Cada 140 ticks, con objetivo, lanza cabezas (`FloreHeadEntity`, máx. 2; enfriamiento de 240 ticks). Comparado: el blossom no busca entidades por tick (solo la búsqueda de objetivo vanilla de zombi y la de ±4 bloques al caer cada spat_block); por tick el zombiflore probablemente es más caro con muchas entidades cerca, y el blossom más caro en partículas del lado del cliente cuando escupe.

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
El usuario probó el jar real (`C:\Projects\cf\test`): las 5 criaturas se ven y se animan bien y lo característico de cada una funciona; las plumas cambian de color y el pincel las alterna. `latest.log` sin errores del mod: solo los avisos conocidos y "Object did not get ID it asked for" de los 5 huevos (el mundo de prueba se creó con el jar de F2 y Forge renumera los ítems nuevos; es normal).

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
El usuario probó el jar real en una instancia aparte (`C:\Projects\cf\test`): pestañas, carapacho, gelatina, sleepy, fiendish, comidas, partículas (sleepy, glass_shard, crit_text, spore), discos, recetas, drops y adornos funcionaron. En el `latest.log` de la prueba no hay errores del mod: solo los avisos conocidos (uniforms de shaders, sonido `entity.stained_glass_retreat`) y la corrección de los `.toml` de configuración creados por primera vez.

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
