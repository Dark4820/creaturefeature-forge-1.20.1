# PROGRESS - Creature Feature port a Forge 1.20.1

## Estado actual
**F1 cerrada (2026-11-02).** Siguiente: F2 (red `SimpleChannel`, NBT en lugar de componentes, partículas con dibujo, shaders core, discos).

## Decisiones del usuario (2026-11-02)
- **D1:** base = código fuente de GitHub. F1–F8 portan el contenido publicado en el jar 1.2.3.3. El contenido ★ queda para **F9** y se decide ahí; la idea del usuario es portar **solo el Powder Skull** (con su botín pasado a las **mazmorras**) y dejar afuera **Trichael, Fish Eye y Wraith** por estar inacabados.
- **D2:** se quitan Toadstool y runic_stone_bricks (Sable). Documentado en PORT_MAP §6.
- **D3:** sin INFESTED/OOZING en blighted_brain; sonidos de 1.20.1 parecidos en lugar de los de breeze; partícula genérica de bloque/ítem en lugar de DUST_PILLAR/ITEM_COBWEB. Cada cambio con `// PORT:`.
- **D4:** no se incluye Galena Hats.
- Cada fase se cierra con `runServer` hasta `Done`.

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
