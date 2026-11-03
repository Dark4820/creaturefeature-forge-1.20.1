# PORT_MAP - Creature Feature NeoForge 1.21.1 → Forge 1.20.1

Fuente: `C:\cf\ref\creaturefeature-master\creaturefeature-master` (GitHub). Jar: `C:\cf\ref\creaturefeature-1.2.3.3.jar`.
Leyenda: ✅ equivalente directo · 🔧 hay que adaptar (equivalente claro) · ⚠️ sin equivalente claro (decisión del usuario) · ★ solo en la fuente, NO en el jar 1.2.3.3.

---

## 1. Fuente vs jar (Fase 0)

Los dos dicen `1.2.3.3`, pero **no son iguales: la fuente de GitHub es más nueva** (trabajo sin publicar).

| | Jar 1.2.3.3 | Fuente master |
|---|---|---|
| Clases Java (sin internas) | 197 | 220 |
| Recursos (assets+data) | 552 | 599 |
| Mixins | 20 | 21 (`OminousItemSpawnerMixin`) |

Todo lo que está en el jar está en la fuente. Lo que **solo está en la fuente** (★):
- Entidades: `TrichaelEntity`, `WraithEntity`, `FishEyeEntity`, `PowderSkullEntity` (criaturas), `CollumnEntity`, `LightningDelayEntity` (auxiliares) + sus renderers, modelos y capas.
- Bloque `obelisk` (`MonolithBlock`), partículas `fisheye` y `red_powder`, shaders `rendertype_entity_{collumn,fisheye,fisheye_itself,red,wraith}`, sonido `fisheye_screech`, huevo `powder_skull_spawn_egg`.
- Loot: `ominous_powder_skull` (modificador sobre el spawner ominoso de las trial chambers) + `OminousItemSpawnerMixin`.
- Diferencias de contenido en `en_us.json`, `sounds.json`, `global_loot_modifiers.json` y `rendertype_entity_fend.vsh` (el resto de los archivos comunes es idéntico salvo finales de línea).
- Trichael, Wraith y FishEye **no tienen huevo ni spawn natural** en la fuente: están a medio hacer.

**Decisión pendiente (D1)**: portar el contenido del jar 1.2.3.3 (lo que juega la gente) o todo lo de la fuente (incluye lo ★ a medio hacer). Propuesta: base = fuente, pero lo ★ en una fase aparte al final (F9), opcional.

---

## 2. Inventario

### Criaturas (con atributos y huevo)
minds, machination, sinister, beauty, blitz, eeper, minedflayer, nothing (NoThing), canary (+ `canary_part`, parte), vertigo, cannonball_crab (huevo `runaway_spawn_egg`), dreamweaver, fiend, fend, friend, mockingbird, pathogen (`PathogenesisEntity`), blossom, saint_solis, detritus, stained_glass, coat_of_arms, powder_skull★, trichael★ (sin huevo), fisheye★ (sin huevo), wraith★ (sin huevo), **toadstool (solo con Sable)**.

Auxiliares (MISC): murky_pearl, feather, vitric_arrow, star, bullet, kicked_block, spat_block, eep (TNT `PrimedEepEntity`), delay★ (`LightningDelayEntity`), collumn★. Además `MindsEntityPart`.

### Ítems (sin contar huevos ni bloques)
music_disc_nothing, music_disc_nan (`NanDiscItem`), bouquet, blighted_brain (comida con INFESTED/OOZING), vertigo_chunk, sleeping_powder, coat_scraps, fiendish_essence, fiendish_soda (`CriticalSodaItem`), living_glass_shards, carapace, vitric_arrow, down_feather, thingamabob, bacterium_ball, flintlock, blitz_rod, dream_silk, solar_shard, dream_catcher (`SpawnerCaptureItem`, componente `dreamcatcher_contents`), the_pill, ectoplasm, murky_pearl, open_mind (componente `opened_mind`), vertigo_horn (instrumento tipo cuerno de cabra), scroll_pan, scroll_trans, scroll_pride, scroll_bi, minedflayer_goop, heavy_axe. 23 huevos (+ toadstool).

### Bloques (todos con ítem)
minedflayer_jelly, carapace_block, eep (TNT), carapace_bricks, carapace_brick_stairs, down_feathers, down_feathers_carpet, mosaic_down_feathers, mosaic_down_feathers_carpet, fiendish_tiles, blind_fiendish_tiles, murky_pearl_tiles, murky_pearl_tile_stairs, murky_pearl_block, dream_silk_spool, wallpaper, solar_block, solar_bricks, doohickey (+ block entity y renderer), obelisk★, **runic_stone_bricks (solo con Sable)**.

### Resto
| Tipo | Contenido | 1.20.1 |
|---|---|---|
| Efectos | fiendish (velocidad +10 %), sleepy (velocidad −50 %, `JUMP_STRENGTH` −100 %) | 🔧 jugador no tiene atributo de salto en 1.20.1: usar `LivingJumpEvent` |
| Partículas (19) | sleepy, crit_text, red_powder★, fisheye★, leaf, casing, glass_shard, glass_shard_sinister, flower, crit, crit_ver, sparkle, sleepy_explode, rabies, toot, cleave, spore, star, spores_spit | ✅/🔧 (las que tengan opciones: `Codec` + `Deserializer`) |
| Shaders core (14) | armor_cutout_evil_cull, ambush, blossom, fend, friend, monochrome, outlined, silk, star + ★ collumn, fisheye, fisheye_itself, red, wraith | 🔧 mismo formato JSON y GLSL 150, `RegisterShadersEvent` existe; los 3 vertex shaders propios (fend, silk, armor_cutout_evil_cull) cambian de marco de coordenadas (ver §6b) |
| Posprocesado (9) | bacte, fiend, friend, haze, man, minedflayer, rabies, sleep, sun (`PostChain` vanilla, NO Veil) | ✅ `PostChain`/`PostPass` iguales en 1.20.1 (mixins 🔧) |
| Sonidos (12) | music_disc.nothing, music_disc.newagenevermore, entity.friend, sinister_hurt/die, fiend_hurt/die, coa_hurt, stained_glass_die, shot, friend_hurt, fisheye_screech★ | ✅ |
| Recetas (47) | 47 JSON, una es la especial `flower_clone` (serializer `crafting_special_flowerclone`, `FlowerCloneRecipe`) | 🔧 formato 1.20.1, `SimpleCraftingRecipeSerializer` existe |
| Logros (9) + 8 triggers | root, roots_man, friendless, gas, horse, pill, renovation, sinister, sleepy; triggers `DummyTrigger` (sleepy, friendless, punch_everyone, gyas, sinister, pill, renovation, nobody) | 🔧 no hay registro `TRIGGER_TYPE`: `CriteriaTriggers.register(...)` y JSON 1.20.1 |
| Discos (2) | nothing (16,3 s), new_age_nevermore (registro `jukebox_song`) | 🔧 `RecordItem(comparador, sonido, props, duraciónTicks)` |
| Materiales de adorno (5) | ectoplasm, scroll_trans, scroll_pan, scroll_pride, scroll_bi + paletas y atlas `armor_trims` | ✅ `trim_material` existe en 1.20.1 (mismo JSON) |
| Loot tables | 23 de entidades (+ `one_egg`, `ominous_powder_skull`★), 20 de bloques | 🔧 carpetas en plural, `random_sequence` se quita/ignora |
| Biome modifiers (27) | spawns + 3 `remove_spawns` | 🔧 tags `c:` → `minecraft:`/`forge:` (tabla abajo) |
| Tags | block `shearable`, `mineable/*`; item `arrows`, `axes`, `trim_materials`; entity `aquatic`, `arthropod`, `can_breathe_under_water`, `zombies` | 🔧 plural; `minecraft:tags/entity_type/aquatic/arthropod/can_breathe_under_water/zombies` no existen en 1.20.1 como tags vanilla (son 1.20.5+): revisar uso |
| Config | CLIENT: remodelfriend, remodelmb, fixsas; SERVER: endreamweaver, pathogen, saint (¡usa la clave "pathogen" otra vez, bug del original!), ff_reloaded | 🔧 `ForgeConfigSpec` |
| Red (9 paquetes) | S→C: VelSync, DeAmp, Rabies, Pathogen, Squashed, GasLeak, ToadstoolRender (Sable); C→S: C2SVelSync, C2SMan | 🔧 `SimpleChannel` |
| Componentes de datos (2) | `opened_mind` (int), `dreamcatcher_contents` (codec) | 🔧 NBT |

### Compatibilidad con otros mods (solo datos)
- **Caverns and Chasms**: `caverns_and_chasms/loot_modifiers/sinister.json` (+75 % de chance agrega `caverns_and_chasms:entities/extra_sinister`, tabla incluida en este mod: 0-2 `silver_ingot`).
- **Oreganized**: 3 loot modifiers (machination, minds, sinister) que agregan `oreganized:entities/extra_*` (tablas que pone Oreganized, no este mod).
- **Blueprint**: tag `blueprint:tags/trim_material/generates_overrides` con los 5 materiales (Blueprint genera los modelos de armadura adornada). `CreatureFeature` importa `BlueprintTrims` pero **no lo usa**.
- Ninguna rompe nada si el mod no está (el modificador apunta a una tabla inexistente → no agrega nada; la tabla de C&C con un ítem inexistente se registra con error en el log: usar condición `forge:mod_loaded` donde se pueda).

---

## 3. Mixins (21; el jar tiene 20)

| # | Mixin | Lado | Objetivo / qué hace | ¿Existe en 1.20.1? |
|---|---|---|---|---|
| 1 | AbstractBlockMixin | común | `BlockStateBase.isValidSpawn` → evita spawns sobre ciertos bloques | ✅ misma firma |
| 2 | AbstractContainerMenuMixin | común | `AbstractContainerMenu.stillValid(access, player, block)` (estático) → menús abiertos lejos (`NoBlockContainerLevelAccess`) | ✅ |
| 3 | JukeBoxPlayableMixin | común (¡llama al cliente!) | `JukeboxPlayable.tryInsertIntoJukebox` → título especial del disco NaN | ✅ **quitado en F2**: `NanDiscItem extends RecordItem` arranca el título en su `useOn` (cliente, por `DistExecutor` → `CFClientItemHooks`) |
| 4 | LivingEntityGoopMixin | común | `LivingEntity.hurt/defineSynchedData/aiStep/causeFallDamage` → estado "goop/delay" sincronizado | 🔧 `defineSynchedData()` sin builder, `causeFallDamage(float,float,DamageSource)` |
| 5 | MonsterEntityMixin | común | `Mob.finalizeSpawn` (HEAD) | 🔧 firma 1.20.1 con `SpawnGroupData, CompoundTag` |
| 6 | OminousItemSpawnerMixin★ | común | `OminousItemSpawner.spawnItem` (trial chambers) | ⚠️ no existe (1.21) |
| 7 | PlayerAdvancementsMixin | común | `PlayerAdvancements.award` (HEAD) | 🔧 recibe `Advancement`, no `AdvancementHolder` |
| 8 | PlayerMixin | común | `Player.tick` → "brainrot"/"rabies" del jugador | ✅ (+ datos sincronizados 🔧) |
| 9 | ShearsItemMixin | común | agrega regla de `Tool` (velocidad 8 en tag `shearable`) y `mineBlock` | 🔧 no hay componente `Tool`: inyectar en `ShearsItem.getDestroySpeed` (y `isCorrectToolForDrops`) |
| 10 | AdvancementTabMixin | cliente | `AdvancementTab.drawContents` → fondo propio | 🔧 verificar firma de `blit` |
| 11 | AdvancementWidgetMixin | cliente | `AdvancementWidget.draw/isMouseOver` → dibujo propio de los logros del mod | 🔧 `AdvancementNode` no existe (usar `Advancement`); `@WrapMethod` requiere MixinExtras (ok) |
| 12 | CameraMixin | cliente | `Camera.setup` y `setRotation` → cámara alterada | 🔧 `setRotation(float,float)`; alternativa Forge `ViewportEvent.ComputeCameraAngles` |
| 13 | CameraTypeMixin | cliente | `CameraType.isFirstPerson` | ✅ |
| 14 | CFGameRendererMixin | cliente | `GameRenderer.render/renderLevel` → ejecuta los 9 `PostChain` y copia profundidad | 🔧 firmas `render(float,long,boolean)`, `renderLevel(float,long,PoseStack)`; revisar ordinales |
| 15 | CFGuiMixin | cliente | `Gui.renderItemHotbar` → hotbar alterada | 🔧 en 1.20.1 es `renderHotbar(float, GuiGraphics)` |
| 16 | CFPostChainMixin | cliente | accessor de `PostChain.passes` + samplers de profundidad | ✅ |
| 17 | FallingBlockEntityRendererMixin | cliente | `FallingBlockRenderer.render` (TAIL) | ✅ |
| 18 | GuiGraphicsMixin | cliente | `GuiGraphics.renderItem(...)` HEAD/RETURN | 🔧 `blitSprite` no existe (sprites son 1.20.2+) |
| 19 | HumanoidArmorLayerMixin | cliente | `renderTrim` → render type propio para los adornos | 🔧 firma 1.20.1 (`ArmorMaterial`, `boolean innerTexture`) |
| 20 | IamActivelyHavingaMigraineMixin | cliente | accessor de `PoseStack.poseStack` | ✅ |
| 21 | LivingEntityRendererMixin | cliente | `@ModifyConstant(-1)` color ARGB en `render` → tinte verde | 🔧 en 1.20.1 son 4 floats: `@ModifyArgs` sobre `renderToBuffer` |

**Mixins nuevos del port** (no están en el original):

| Mixin | Lado | Qué hace | Fase |
|---|---|---|---|
| SleepyJumpMixin | común | `LivingEntity.getJumpPower` → 0 con el efecto sleepy (reemplaza el modificador de `JUMP_STRENGTH`, que en 1.20.1 no existe para jugadores ni mobs) | F1 |
| LivingEntitySwingMixin | común | `LivingEntity.getCurrentSwingDuration` (privado en 1.20.1) ×2 para el canary, como el override del original | F4 |

Además hay que corregir: el original usa `java.awt.Color` en un renderer (funciona pero conviene quitarlo) y llama a `CreatureFeatureClient.PROXY` desde código común (`JukeBoxPlayableMixin`, ítems).

---

## 4. Diferencias 1.21.1 → 1.20.1 que afectan al mod

| Tema | 1.21.1 (original) | 1.20.1 (port) | Alcance |
|---|---|---|---|
| Red | `CustomPacketPayload` + `StreamCodec` + `RegisterPayloadHandlersEvent` | `SimpleChannel` + `encode/decode/handle` | 9 paquetes, ~26 archivos los envían 🔧 |
| Componentes de ítem | `DataComponentType` (2 propios) + `jukeboxPlayable`, `food`, `attributes` | NBT; `RecordItem`; `FoodProperties` 1.20.1 (`alwaysEat`); `AxeItem(tier, dmg, speed, props)` | ~9 archivos 🔧 |
| Discos | registro de datos `jukebox_song` | `RecordItem` + duración en ticks | 2 discos 🔧 |
| Shaders | `RegisterShadersEvent`, `ShaderInstance`, `RenderType.create` (5 args), constantes de `RenderStateShard` públicas por el AT de NeoForge; entidades con posiciones alineadas al mundo y `fog_distance(pos, shape)` | `create` de 7 args (`false, false`); `CFRenderTypes extends RenderType` para llegar a las constantes `protected`; posiciones rotadas por la cámara + `IViewRotMat`, `fog_distance(ModelViewMat, pos, shape)` | 🔧 hecho en F2 |
| Posprocesado | `PostChain` vanilla vía mixin a `GameRenderer` | igual, firmas distintas | 🔧 |
| Vértices | `addVertex().setColor().setUv()...` | `vertex().color().uv()...endVertex()` | **41 archivos** (mecánico) 🔧 |
| Modelos | `renderToBuffer(..., int color)` | `renderToBuffer(..., r,g,b,a)` | todos los modelos 🔧 |
| Cámara | `Camera.setRotation(y, x, roll)` | `setRotation(y, x)`; roll con `ViewportEvent.ComputeCameraAngles` | 🔧 |
| `DeltaTracker` | `render(DeltaTracker, boolean)` | `partialTick` float | 3 archivos 🔧 |
| Registros | `DeferredRegister.Items/Blocks`, `DeferredHolder`, `Holder<MobEffect>` | `DeferredRegister` + `RegistryObject`; efecto directo | todo init 🔧 |
| Triggers | registro `TRIGGER_TYPE` | `CriteriaTriggers.register` (con id propio) | 🔧 |
| Entidades | `defineSynchedData(Builder)`, `EntityType.Builder.eyeHeight/passengerAttachments/ridingOffset`, `finalizeSpawn` sin NBT | `defineSynchedData()`, overrides `getStandingEyeHeight`/`getPassengersRidingOffset`/`getMyRidingOffset` | 33 entidades 🔧 |
| Spawns | `RegisterSpawnPlacementsEvent` + `SpawnPlacementTypes` | `SpawnPlacementRegisterEvent` + `SpawnPlacements.Type` | 🔧 |
| Atributos | `Attributes.STEP_HEIGHT`, `JUMP_STRENGTH` (genérico) | `ForgeMod.STEP_HEIGHT_ADDITION`; salto con `LivingJumpEvent` | 🔧 |
| Modificadores de atributo | id `ResourceLocation` | `UUID` + nombre | 🔧 |
| Daño | `DamageSource` (igual que 1.20.1) | igual | ✅ |
| Datos | carpetas singulares, `"id"`, `neoforge:*`, tags `c:` | plural, `"item"`, `forge:*`, tags `forge:`/`minecraft:` | 🔧 |
| Loot modifiers | `neoforge:add_table` | **no existe**: GLM propio `creaturefeature:add_table` | 🔧 |
| Pestaña creativa | `BuildCreativeModeTabContentsEvent.insertAfter/insertBefore` (74 usos) | `event.getEntries().putAfter/putBefore` | 🔧 |
| Config | `ModConfigSpec` + `modContainer.registerConfig` | `ForgeConfigSpec` + `ModLoadingContext.registerConfig` | 🔧 |
| Pantalla de configuración | `ConfigurationScreen` de NeoForge (botón "Config" en la lista de mods) | Forge 1.20.1 no trae una genérica: el config se edita en `config/creaturefeature-client.toml` y `world/serverconfig/creaturefeature-server.toml` (o con un mod como Configured) | ⚠️ menor, sin código |
| Contenido vanilla 1.21 usado | `Items.MACE`, `BREEZE_ROD` (posición en pestaña), sonidos `BREEZE_*` (Beauty, Blitz, Coat of Arms), partículas `DUST_PILLAR` (8 usos) e `ITEM_COBWEB`, efectos `INFESTED` y `OOZING` (blighted_brain), trial chambers | no existen | ⚠️ D3 |

### Biome tags `c:` → 1.20.1 (propuesta, lo decido yo salvo que digas otra cosa)
| Original | 1.20.1 |
|---|---|
| `#c:is_overworld` | `#minecraft:is_overworld` |
| `#c:is_jungle` / `is_savanna` / `is_taiga` / `is_beach` / `is_nether` | `#minecraft:is_jungle` / `is_savanna` / `is_taiga` / `is_beach` / `is_nether` |
| `#c:is_mountain` / `is_desert` / `is_plains` | `#forge:is_mountain` / `#forge:is_desert` / `#forge:is_plains` |
| `#c:is_outer_end_island` | lista: `end_highlands`, `end_midlands`, `small_end_islands`, `end_barrens` |
| `#c:is_floral` | lista: `flower_forest`, `sunflower_plains`, `meadow`, `cherry_grove` |
| `#c:no_default_monsters` | lista: `mushroom_fields`, `deep_dark` |

---

## 5. Dependencias

Lo que realmente usa el código (imports y `build.gradle`), más allá de lo que lista `gradle.properties`:

| Dependencia | ¿Obligatoria? | Para qué | ¿Forge 1.20.1? | Decisión |
|---|---|---|---|---|
| **Galena Hats** (`dev.galena:hats-neoforge:1.21.1-1.2.5`, jarJar) | No usada por el código (0 referencias). Va incluida en el jar porque Team Galena la mete en todos sus mods | Galeras cosméticas para los supporters de Team Galena (mod aparte, `galena_hats`) | ✅ `dev.galena:hats-forge:1.20.1-1.3.0` en `registry.somethingcatchy.net` | D4: incluirla por jarJar o no |
| **Sable** (ryanhcode; también es el `maven.modrinth:T9PomCSv` del build) | Opcional (todo detrás de `ModList.isLoaded("sable")`) | Física de "sub-niveles": entidad **Toadstool** (+ huevo, spawn, paquete `ToadstoolRender`) y bloque **runic_stone_bricks** | ❌ solo Fabric/NeoForge 1.21.1 | D2: quitar |
| **Sable Companion** | Opcional (va con Sable) | utilidades matemáticas para lo anterior | ❌ | se va con Sable |
| **Veil** (FoundryMC) | **No se usa**: 0 imports. Los shaders usan el sistema vanilla (`ShaderInstance`, `PostChain`) | (sería renderizado) | Modrinth: solo 1.21.1; maven de BlameJared: solo 1.21/1.21.1 | no hace falta |
| **Create / Ponder / Registrate / Simulated** | **No se usan**: 0 imports, ni siquiera en `build.gradle` (restos del entorno de Sable) | — | (Create sí existe en 1.20.1, pero no importa) | no hace falta |
| **JEI** | **No se usa** (solo un ejemplo comentado) | — | — | no hace falta |
| **Blueprint** (Team Abnormals) | Opcional, solo datos | tag `generates_overrides` de los materiales de adorno | ✅ 7.1.5 para Forge 1.20.1 | dejar el JSON; sin Blueprint los ítems de armadura adornados con estos materiales muestran el icono sin adorno (el adorno sobre el cuerpo se ve igual) |
| **Forge Config API Port** | En `build.gradle` pero innecesaria en NeoForge | — | — | no hace falta (`ForgeConfigSpec` nativo) |
| **MixinExtras** | **Obligatoria** (`@WrapOperation`, `@ModifyReturnValue`, `@WrapMethod`) | mixins | ✅ 0.4.1 jarJar (ya configurado) | hecho |

Conclusión: **no hay ninguna dependencia obligatoria que falte en Forge 1.20.1.**

---

## 6. Decisiones (tomadas por el usuario el 2026-11-02)

- **D1 – Base del port = código fuente de GitHub.** F1–F8 portan el contenido publicado en el jar 1.2.3.3. El contenido ★ se decide en F9 (idea del usuario: solo Powder Skull, con su botín pasado a las mazmorras; Trichael, Fish Eye y Wraith quedan afuera por inacabados).
- **D2 – Sable: se quita.** Ver "Contenido quitado" abajo.
- **D3 – Contenido vanilla de 1.21:** se quitan `INFESTED` y `OOZING` de blighted_brain; los sonidos `BREEZE_*` se cambian por sonidos parecidos de 1.20.1; `DUST_PILLAR` → `ParticleTypes.BLOCK` (mismo bloque) e `ITEM_COBWEB` → `ParticleTypes.ITEM` con una telaraña. Cada cambio lleva `// PORT:`.
- **D4 – Galena Hats: no se incluye.**

### Contenido quitado (no existe en el port)
| Contenido | Motivo |
|---|---|
| Entidad `toadstool` + `toadstool_spawn_egg` + spawn (`biome_modifier/toadstool.json`) + loot `entities/toadstool` + paquete `ToadstoolRenderPayload` + `ToadstoolEntityRenderer` | depende de Sable (física de sub-niveles), que no existe en 1.20.1 (D2) |
| Bloque `runic_stone_bricks` (`RunicStoneBricksBlock`) + su loot y modelos | depende de Sable (D2) |
| `misc/SableCarrier`, todos los `ModList.isLoaded("sable")` | ídem |
| Galena Hats (jarJar) | D4 |

## 6d. Agregados del port (no están en el original; van en el mod publicado)
| Agregado | Detalle |
|---|---|
| Compatibilidad del ectoplasma con **Goety** (pedido del usuario, 2026-11-02) | Tag `data/forge/tags/items/ectoplasm.json` = `creaturefeature:ectoplasm` + `goety:ectoplasm` (opcional, `"required": false`: sin Goety el tag carga solo con el de este mod). Las recetas del mod que usan ectoplasma (`bi/pan/pride/trans_scroll`, `torch`) lo piden por `#forge:ectoplasm`; lo hace `tools/convert_resources.py` (`SHARED_TAGS`), solo en ingredientes. Goety 2.5.59.2: su ectoplasma es `goety:ectoplasm`; lo usan 92 ingredientes en 59 rituales (`goety:ritual`) y 15 recetas de mesa, todos como `{"item": "goety:ectoplasm"}` (sus rituales sí aceptan tags: 275 de 337 ya usan alguno). Que Goety acepte el de este mod: pendiente de decisión del usuario |
| Prueba automática de dibujo `runClientRenderSmoke` (solo desarrollo, `src/gametest/.../client/RenderSmokeTest`, carpeta `run-smoke`) | crea un mundo plano, invoca todas las criaturas frente a la cámara con un jugador en supervivencia que no puede morir, las deja atacar 30 s, saca capturas en `run-smoke/screenshots` y cierra el juego; `CF_RENDER_SMOKE_OK` en el log = ningún renderer crasheó. Reproduce el crash del coat of arms de F4 sin el arreglo |

## 6c. Criaturas: diferencias de API resueltas (F3+)
| Original (1.21) | Port (1.20.1) |
|---|---|
| `EntityType.Builder.eyeHeight/passengerAttachments/ridingOffset` | overrides `getStandingEyeHeight`, `getPassengersRidingOffset`, `getMyRidingOffset` con los mismos valores |
| `SpawnEggItem(EntityType)` | `ForgeSpawnEggItem(RegistryObject)` (los ítems se registran antes que las entidades; Forge registra los colores) |
| `RegisterSpawnPlacementsEvent` / `SpawnPlacementTypes` | `SpawnPlacementRegisterEvent` / `SpawnPlacements.Type` |
| atributos en `CreatureFeature.registerAttributes` | `CFEvents.ModBus.registerAttributes` (mismo evento) |
| `SpawnPlacements.isSpawnPositionOk(type, level, pos)` | `NaturalSpawner.isSpawnPositionOk(getPlacementType(type), level, pos, type)` |
| `getDefaultGravity()` (beauty) | atributo `ForgeMod.ENTITY_GRAVITY` con la misma fórmula (lo lee `LivingEntity.travel`) |
| `MeleeAttackGoal` usa `isWithinMeleeAttackRange` (sinister lo anula) | en 1.20.1 usa `getAttackReachSqr`: `SinisterMeleeGoal` devuelve −1, así el sinister nunca pega cuerpo a cuerpo (como el original) |
| `BREEZE_WHIRL` (beauty) | `PLAYER_ATTACK_SWEEP` (D3) |
| `DUST_PILLAR` (detritus) | `ParticleTypes.BLOCK` (D3) |
| vértices `addVertex().setColor()…` / modelos `renderToBuffer(…, int color)` | `vertex().color()…endVertex()` / `renderToBuffer(…, r, g, b, a)` |
| `ByteBufferBuilder` (buffers propios de detritus, fend y friend) | `BufferBuilder`. El de fend no lleva `entityWraithCutout` (shader ★, F9; solo lo usa el wraith) |
| `Attributes.STEP_HEIGHT` (friend 10, blitz 1,5) | `setMaxUpStep` en el constructor |
| `getDefaultGravity()` constante (canary 0,01, canary_part 0, coat_of_arms ×0,6) | valor base de `ENTITY_GRAVITY` en sus atributos; `getGravity()` (fend) → `getAttributeValue(ENTITY_GRAVITY)`; en `KickedBlockEntity` (proyectil) → 0 |
| `getCurrentSwingDuration` (público en 1.21, el canary lo duplica) | privado en 1.20.1: `LivingEntitySwingMixin` |
| `Mob.stopInPlace()` (fiend, coat_of_arms) | su cuerpo: `getNavigation().stop()` + `setXxa/setYya/setSpeed(0)` |
| `noBlockCollision` (fend) | el mismo recorrido sobre `getBlockCollisions` |
| `ProjectileWeaponItem.customArrow(arrow, ammo, weapon)` / `getMobArrow(…, weapon)` (fend) | `BowItem.customArrow(arrow)` / `getMobArrow(…)` (como el esqueleto de Forge 1.20.1) |
| `MobSpawnType.isSpawner` (fend) | `== SPAWNER` (1.20.1 no tiene trial spawners) |
| `BlockState.isEmpty()` (fiend) | `isAir()` |
| `triggerOnDeathMobEffects` (eeper) | sin llamada: en 1.20.1 ningún efecto reacciona a la muerte |
| `dropCustomDeathLoot(ServerLevel, DamageSource, boolean)` / `finalizeSpawn(…)` | `dropCustomDeathLoot(DamageSource, int looting, boolean)` / `finalizeSpawn(…, CompoundTag)` |
| `getAddEntityPacket(ServerEntity)` (kicked_block) / sin override (bullet) | `getAddEntityPacket()`; en 1.20.1 es abstracto en `Entity`, así que bullet lo implementa |
| `EventHooks.onProjectileImpact` + `hitTargetOrDeflectSelf` | `ForgeEventFactory.onProjectileImpact` + `onHit` |
| `BREEZE_HURT/DEATH` (blitz), `BREEZE_WHIRL` (coat_of_arms) | `BLAZE_HURT/DEATH` (el blitz_rod ya reemplaza a la vara de breeze), `PLAYER_ATTACK_SWEEP` (D3) |
| Consumer pedido antes de dibujar otra cosa (coat of arms: la capa antes de los flintlocks) | en 1.20.1 pedir otro render type al buffer compartido cierra el lote anterior y el consumer viejo crashea ("BufferBuilder not started"): se pide justo antes de usarlo. Revisados todos los renderers: solo el coat of arms lo tenía |
| Friend fuera de Fabulous: buffer propio que se vuelca en `FRIEND_TARGET` y solo lo muestra la post chain "friend" | hasta F7 (post chains) siempre el camino Fabulous del original (buffer normal); sin eso el friend era invisible (`// PORT: TODO F7`) |
| `Item.Properties().durability(4).stacksTo(1)` (flintlock) | `durability(4)`: en 1.20.1 `stacksTo` después de `durability` lanza "Unable to have damage AND stack" (el mod no cargaba) |
| `PacketDistributor.sendToPlayer` | `CFNetwork.sendToPlayer` |

## 6b. Cambios en archivos JSON (no admiten `// PORT:`)
| Archivo | Cambio | Motivo |
|---|---|---|
| `assets/minecraft/atlases/armor_trims.json`, `blocks.json` | `blueprint:paletted_permutations` → `minecraft:paletted_permutations`; en `armor_trims` la fuente `directory` se cambia por la lista explícita de los 16 patrones vanilla (×2 con `_leggings`) | el tipo de Blueprint hace que los adornos dependan de Blueprint; con el vanilla funcionan sin él (los patrones de otros mods no reciben los colores de este mod) |
| `sounds.json` | sin `entity.fisheye_screech` | ★ (F9) |
| `trim_material/*.json` | sin cambios (mismo formato en 1.20.1) | — |
| `ru_ru.json`, `en_us.json` | sin cambios (tienen claves de contenido quitado/★: no molestan) | — |
| Recetas, loot tables, tags | formato y carpetas de 1.20.1 (ver §4) | 1.20.1 |
| `shaders/core/rendertype_entity_fend.vsh`, `rendertype_entity_silk.vsh`, `rendertype_armor_cutout_evil_cull.vsh` (+ sus `.json`) | `uniform mat3 IViewRotMat` (también en el JSON); `position21 = IViewRotMat * Position` reemplaza a `Position` en los cálculos; `fog_distance(ModelViewMat, …)`; fend vuelve al marco 1.20.1 con `transpose(IViewRotMat)` antes de `gl_Position`. Comentarios `// PORT:` dentro del GLSL | en 1.20.1 los vértices de entidades llegan rotados por la cámara; sin esto el shader no compila (crash del cliente) y los efectos por altura (`OffYPosition`) usarían el eje equivocado. `tools/convert_resources.py` no los vuelve a copiar (`HAND_EDITED`) |
| `models/item/flintlock.json` | `"loader": "neoforge:separate_transforms"` → `"forge:separate_transforms"` (lo hace el script) | nombre del loader en Forge 1.20.1 (mismo formato) |
| `data/minecraft/tags/items/music_discs.json` (nuevo) | los 2 discos | en 1.20.1 el tocadiscos solo acepta ítems de `#minecraft:music_discs`; sin el tag el disco se consumía y el tocadiscos quedaba vacío (lo detectó un GameTest) |
| `lang/*.json` | se agregan `item.creaturefeature.music_disc_*.desc` copiando `jukebox_song.creaturefeature.*` (lo hace el script) | el tooltip del disco en 1.20.1 usa `<id del ítem>.desc` |
| `particles/reveal.json` y `textures/particle/reveal.png` | no se copian | el original no registra ningún tipo `reveal`; en 1.20.1 un JSON de partícula sin tipo hace fallar la recarga de recursos del cliente |
| `loot_tables/entities/*.json` (F3+) | `enchanted_count_increase` (looting) → `looting_enchant` con el mismo `count`; `random_chance_with_enchanted_bonus` → `random_chance_with_looting` con `chance` = `unenchanted_chance` y `looting_multiplier` = (`base` + 2·`per_level_above_first` − `unenchanted_chance`)/3. Las entradas de ítems todavía no registrados se quitan y el script las lista (vuelven al correrlo en su fase) | formato 1.20.1. La probabilidad es **igual** a la del original cuando `per_level_above_first` = `base` − `unenchanted_chance` (machination, sinister); si no, coincide en Botín III (detritus: 0,19/0,24/0,29 en lugar de 0,15/0,22/0,29). Beauty no suelta `bouquet` hasta F6 |
| `data/minecraft/tags/entity_type/zombies.json` | no se copia | el tag no existe en 1.20.1; detritus pasa a ser no-muerto con `getMobType() = UNDEAD` (minds ya lo es por ser `Zombie`; blossom, que también está en el tag, se revisa en F5) |

---

## 7. Plan por fases

Cada fase: compila, GameTests, `runServer` hasta `Done`, pruebas en el juego, commit local.

| Fase | Contenido | Prueba en el juego al cerrar |
|---|---|---|
| **F0** ✅ | Análisis, esqueleto Forge 1.20.1 (Java 17 por toolchain, MixinExtras, GameTests) | el mod aparece en la lista de mods |
| **F1** ✅ | Registros base: sonidos, efectos (lógica simple), bloques y sus ítems, ítems simples (sin lógica de entidades), tags, recetas, loot de bloques, materiales de adorno, idioma, modelos/texturas, pestaña creativa, config, manejadores de color de las plumas (agregados después de la prueba en el juego: `blocks/FeatherTints` + `CreatureFeatureClient`) | `/give` de bloques e ítems, colocar/romper bloques (drops), recetas en la mesa, adornar armadura con ectoplasma/pergaminos, `/effect give @s creaturefeature:sleepy` |
| **F2** ✅ | Infra: red `SimpleChannel` (8 paquetes), NBT en lugar de componentes, partículas (tipos + providers), `CFRenderTypes` + 9 shaders core registrados (los 5 ★ quedan para F9), discos (`RecordItem`), accessors | `/particle creaturefeature:...`, discos en el tocadiscos (incl. título del NaN) |
| **F3** ✅ | Criaturas lote 1: minds (+part), machination, sinister, beauty, detritus + loot + huevos + atributos + spawn placements (sin spawn natural: F8) | `/summon` y huevos, pelear, drops |
| **F4** ✅ | Lote 2: fiend, fend, friend, blitz, eeper, nothing, canary (+part), vertigo, **coat_of_arms** + `bullet`, **`kicked_block`** (pasó de F5: lo patea el fiend) y el ítem **flintlock** (pasó de F6: lo sostienen fiend y coat_of_arms) | `/summon`, ver y pelear |
| **F5** | Lote 3: dreamweaver, cannonball_crab, minedflayer, mockingbird, pathogen, saint_solis, stained_glass, **blossom** (pasó de F3: escupe `spat_block`) + spat_block, murky_pearl, vitric_arrow, eep (TNT), **feather** (mockingbird) y **star** (saint_solis), que estaban en F4 | idem |
| **F6** | Ítems con lógica (el flintlock ya está desde F4): vertigo_horn (vuelve con él el botín de cuernos del vertigo), dream_catcher, open_mind, the_pill, bouquet (`FlowerCloneRecipe`), fiendish_soda, bacterium_ball, solar_shard, living_glass, heavy_axe, doohickey (BE + renderer) + mixins comunes (1,2,4,5,7,8,9) | usar cada ítem; atrapar un spawner con el dream catcher |
| **F7** | Efectos de pantalla y cliente: 9 `PostChain` + mixins de cliente (10-21), cámara, hotbar, adornos | ver cada efecto (rabia, haze, sueño, fiend, minedflayer...) en el cliente |
| **F8** | Spawns naturales (27 biome modifiers), GLM `add_table` + compat Oreganized / C&C / Blueprint, logros y triggers | `/locate biome` + esperar spawns; matar sinister con C&C instalado; árbol de logros |
| **F9** (opcional, según D1) | Contenido ★: powder_skull, trichael, fisheye, wraith, obelisk, collumn, delay | `/summon` |
| **F10** | Pulido: servidor dedicado con cliente, modpack real (Oculus/Embeddium si están), versión final | sesión de juego normal |

## 8. Riesgos
1. **Renderizado**: 41 archivos con la API de vértices nueva y todos los modelos con color `int`: mucho trabajo mecánico; errores dan modelos invisibles/negros, no crashes.
2. **Mixins de cliente con ordinales** (`CFGameRendererMixin`, `CFGuiMixin`): hay que reubicarlos mirando el bytecode de 1.20.1; si fallan, con `defaultRequire: 1` el juego crashea al arrancar (mejor que fallar en silencio).
3. **Shaders con Oculus/Iris** en el modpack: los `RenderType` con shader propio y los `PostChain` pueden verse mal o no verse con un shaderpack activo. Probar en F7/F10.
4. **Código de cliente mezclado en el común** en el original (`PROXY` usado desde ítems, entidades y mixins comunes): cada uso hay que separarlo o crashea el servidor dedicado.
5. **Paquetes C→S** (`C2SVelSync`, `C2SMan`): el original confía en la velocidad que manda el cliente; mantener el comportamiento, pero validar mínimos.
6. Contenido ★ de la fuente está a medio hacer (sin huevos ni spawns): puede tener bugs propios.
7. Tags de entidad `minecraft:aquatic/arthropod/...` que en 1.20.1 no son vanilla: hay que ver qué lógica dependía de ellos.
