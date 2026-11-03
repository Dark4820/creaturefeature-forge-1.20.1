# Creature Feature — Forge 1.20.1 port

An **unofficial** port of [Creature Feature](https://github.com/TerriblyBadCoder/creaturefeature) to
**Minecraft 1.20.1 / Forge 47.4+**.

## Credits

**Creature Feature is made by AtiredDev and Team Galena.** Every creature, model, texture, sound, shader and idea
in this mod is theirs. The original is a NeoForge 1.21.1 mod released under the **MIT license** (declared in its
mod metadata). This port only translates their code and data to Forge 1.20.1.

Please report problems of this port to the port, not to the original authors.

## Version

`1.2.3.3-forge.1` = based on the original source of Creature Feature 1.2.3.3 (GitHub; it is a bit newer than the
published 1.2.3.3 jar), first Forge 1.20.1 release.

## Install

- Minecraft 1.20.1 with Forge 47.4 or newer.
- Use `creaturefeature-1.2.3.3-forge.1-all.jar` (it carries MixinExtras inside). Install it on the server and on
  every client.
- No other mod is required. Optional compat: Oreganized and Caverns and Chasms (extra drops), Blueprint (armour
  trim item models), Goety (its ectoplasm and this mod's are interchangeable).

## Configuration

- `config/creaturefeature-client.toml`: `remodelfriend`, `remodelmb`, `fixsas` (as in the original).
- `<world>/serverconfig/creaturefeature-server.toml`: the original's `endreamweaver`, `pathogen`, `ff_reloaded`,
  plus (port only) a `[creatures]` section with one switch per creature, all `true` by default. A creature set to
  `false` never spawns (naturally, from spawners or from other creatures of the mod) and, if it already exists,
  vanishes without drops. Spawn eggs and `/summon` still work for admins (`/summon` with NBT needs
  `Tags:["creaturefeature_allowed"]`). Put the file in `defaultconfigs/` to apply it to new worlds.
- Forge 1.20.1 has no built-in config screen: edit the files.

## What changes from the original

See [CHANGES.md](CHANGES.md): removed, adapted, fixed and added.

## License

MIT, see [LICENSE](LICENSE).
