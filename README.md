# Creature Feature — Forge 1.20.1 port

> **This is an UNOFFICIAL port.** It is not made, maintained or supported by the original authors.
> Original mod: **[Creature Feature](https://github.com/TerriblyBadCoder/creaturefeature)** by **AtiredDev and Team Galena**
> (NeoForge 1.21.1, MIT license). The source code is published openly. This port is not endorsed by or affiliated
> with the original authors.

A port of Creature Feature to **Minecraft 1.20.1 / Forge 47.4+**. In the mod list and in JEI it shows as
**Creature Feature**, like the original; the mod description and credits say it is this unofficial port.

## Credits

**Creature Feature is made by AtiredDev and Team Galena.** Every creature, model, texture, sound, shader and idea
in this mod is theirs. The original is a NeoForge 1.21.1 mod released under the **MIT license** (declared in its
mod metadata). This port only translates their code and data to Forge 1.20.1.

## Bug reports

**Report problems of this port here, in this repository's
[issues](https://github.com/Dark4820/creaturefeature-forge-1.20.1/issues), not to the original authors.** Bugs that
only happen with this port are not theirs to fix.

## How it was made

The port was made with the help of AI (Claude Code, by Anthropic) and reviewed and tested by a person: automated
GameTests (59) and a render test run on every change, and the mod was played in game, alone and inside a large
modpack (371 mods, with Oculus shaders). Every code change from the original is marked with a `// PORT:` comment, and
`docs/PORT_MAP.md` maps the original to the port file by file (the docs are in Spanish).

## Version

`1.2.3.3-forge.1` = based on the original source of Creature Feature 1.2.3.3 (GitHub; it is a bit newer than the
published 1.2.3.3 jar), first Forge 1.20.1 release.

## Install

- Minecraft 1.20.1 with Forge 47.4 or newer.
- Use `creaturefeature-forge-1.20.1-1.2.3.3-forge.1.jar` (it carries MixinExtras inside; the `-dev` jar is not for players). Install it on the server and on
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
