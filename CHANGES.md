# Changes from the original Creature Feature

Base: the GitHub source of Creature Feature (NeoForge 1.21.1, `mod_version=1.2.3.3`), which is a little newer than
the published 1.2.3.3 jar. Registry names (`creaturefeature:<id>`), packages and classes are kept. Every code change
is marked `// PORT:` in the source; the full technical map is `docs/PORT_MAP.md`.

## Removed

| What | Why |
|---|---|
| Toadstool (entity, spawn egg, spawn, loot, render packet, renderer), Runic Stone Bricks, `SableCarrier` | they are built on Sable (sub-level physics), which only exists for Minecraft 1.21.1 (NeoForge / Fabric); there is no Sable for Forge 1.20.1, so they cannot be present even with both mods installed |
| Galena Hats (Team Galena's supporter top hats, bundled with jarJar in the original) | not included, not even as a dependency. The original only bundled it; its code never uses it, so nothing else changes |
| Unfinished content that exists only in the source (not in the published jar): Powder Skull (entity, egg, red powder particle, red shader, ominous trial spawner loot and `OminousItemSpawnerMixin`), Trichael, Fish Eye (particle, `fisheye_screech` sound), Wraith (and its render type), Obelisk block, collumn, delay | unfinished in the source (the Powder Skull, for example, chases players but never attacks, has no loot, no natural spawn and no sounds of its own) |
| `particles/reveal.json` | the original registers no `reveal` particle type; in 1.20.1 that breaks the client resource reload |
| Entity tag `minecraft:zombies` file | the tag does not exist in 1.20.1 (see "Adapted": undead mob type) |

## Adapted (1.20.1 means; same behaviour unless noted)

- **Networking:** `CustomPacketPayload`/`StreamCodec` → a `SimpleChannel` with the same packets (8; the ninth was the Toadstool one); client-to-server
  packets are checked on the server.
- **Item data components** → NBT (dream catcher contents, open mind state, horn instrument, spawner data, damage).
- **Music discs:** `jukebox_song` data → `RecordItem` + `#minecraft:music_discs` tag; the NaN disc's special title
  starts from the item (the original used a mixin on 1.21's jukebox component).
- **Rendering:** the 1.21 vertex API, `renderToBuffer` colours (four floats), entity shaders (1.20.1 camera-rotated
  positions, three-argument fog), the post-processing chains (`PostChain.setUniform` replaced by a per-pass helper),
  `Camera.setRotation` without roll, `DeltaTracker` → partial ticks.
- **Entities:** eye height and riding offsets as overrides, custom gravity through `ForgeMod.ENTITY_GRAVITY`, step
  height with `setMaxUpStep`, spawn eggs with `ForgeSpawnEggItem`, 1.20.1 spawn placement event, melee reach by
  distance where 1.21 used hit boxes (sinister, stained glass), arrow and projectile API differences.
- **Undead and water mob types** (1.21 entity tags `zombies`, `aquatic`, `can_breathe_under_water`): detritus is
  undead through `getMobType()`; the cannonball crab is a water mob that breathes underwater.
- **Blighted brain effects:** it keeps the original's Nausea, Infested and Oozing (same durations, 80 % each). Infested
  and Oozing are 1.21 vanilla effects: with **Trials Chambers** installed (optional) the brain gives its
  `trials:infested` and `trials:oozing`; without it, the port's own `creaturefeature:infested` and
  `creaturefeature:oozing`, copies of 1.21's behaviour (Infested: 10 % chance per hit taken to release 1-2 silverfish;
  Oozing: two size-2 slimes on death, fewer if `maxEntityCramming` would be exceeded). Their icons draw a vanilla item
  (silverfish spawn egg, slime ball).
- **1.21 vanilla content:** `BREEZE_*` sounds → similar 1.20.1 sounds (blaze, player sweep); `DUST_PILLAR` and
  `ITEM_COBWEB` particles → block / item particles of the same block or item.
- **Recipes using 1.21 items:** Open Mind uses a crafting table instead of a crafter; the Heavy Axe uses a netherite
  ingot instead of a heavy core.
- **Creative tabs:** the Heavy Axe goes after the netherite axe (there is no mace in 1.20.1).
- **Data:** plural folders, `"item"` instead of `"id"`, `forge:` instead of `neoforge:`; loot functions and
  conditions translated (looting); 1.21 `c:` biome tags translated to vanilla / Forge tags, or to the same biome lists
  where 1.20.1 has no tag (outer End islands, floral biomes, biomes without default monsters). Same biomes, weights
  and group sizes.
- **Loot compat** with Oreganized and Caverns and Chasms: Forge 1.20.1 has no `add_table` loot modifier, so the port
  ships its own (`creaturefeature:add_table`); the other mods' items are referenced through optional tags, so nothing
  fails to load when those mods are absent.
- **Advancements and triggers:** same tree and triggers (`CriteriaTriggers.register`); the two advancement-screen
  mixins adapted to 1.20.1's `AdvancementWidget`.
- **Config:** `ForgeConfigSpec`, same keys and defaults (including the original's reuse of the `pathogen` key for the
  Saint Solis setting). No in-game config screen (Forge 1.20.1 has none).
- **Shears:** the 1.21 tool component rule (speed 8 on `#creaturefeature:shearable`) → a mixin on `getDestroySpeed`.
- **Detritus loot with Looting I and II:** 1.21's "random chance with enchanted bonus" has no exact 1.20.1 equivalent;
  the port uses `random_chance_with_looting`. One of the detritus' drops comes out at 19 % / 24 % with Looting I / II
  instead of 15 % / 22 % (the same 29 % with Looting III, and the same chance without Looting). Every other creature's
  loot chances are exactly the original's.
- **`pathogen = true` (server config):** the original limits that spawn to Forge's "temperate overworld" biome tag,
  which 1.20.1 does not have; the port uses "overworld and neither hot nor cold".
- **Armour trims of the mod's materials:** the original's texture atlases used a Blueprint source type; the port uses
  the vanilla one with the same patterns, so the trims no longer need Blueprint (Blueprint stays optional).
- **No visible difference:** the fend no longer checks for trial spawners (they do not exist in 1.20.1); the eeper
  no longer notifies effects on death (in 1.20.1 no effect reacts to death).
- **Client code split from common code** (the original called client classes from items, mixins and the mod class):
  it now lives in client-only classes, so dedicated servers do not crash.

## Fixed (bugs of the original)

- A Murky Pearl fired by a dispenser was created as a snowball; it is now a murky pearl.
- After a resource reload (F3+T) the canary's haze screen effect kept a reference to the old post chain; it now
  follows the new one.
- On a client joining a dedicated server, the mod's items were missing from the Tools & Utilities tab (and from JEI):
  the vertigo horn was placed by looking up the goat horn tag before the server had sent it, which threw and skipped
  the rest of that tab. When the tag is not there yet, the horn now goes to the end of the tab.

## Added (not in the original)

- **One switch per creature** in the server config (`[creatures]`, all on by default = original behaviour).
- **Trials Chambers compat (optional):** see "Blighted brain effects"; nothing is required and nothing fails without it.
- **Goety compat:** this mod's recipes accept either ectoplasm (`#forge:ectoplasm`), and two 1:1 conversion recipes
  exist when Goety is installed.
- `creaturefeature:add_table` loot modifier (see "Adapted").
- The MIT license text, this file and a README inside the jar.
- An opt-in in-game self-test (`-Dcreaturefeature.selftest=true`, for a throwaway world only): in singleplayer it checks
  the blighted brain effects; on a dedicated server started with the same property, and a client connected with it,
  it checks what travels between them (effects, screen effects, the mod's packets, every creature). It does nothing
  unless that property is set.

Development-only tools (GameTests, an automatic render test, a performance test) live in a separate source set and are
not in the jar.
