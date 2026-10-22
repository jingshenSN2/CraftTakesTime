# Craft Takes Time

CraftTakesTime makes manual crafting take time. Each Minecraft version has its own project directory.

## NeoForge 2.0.0-beta.2

Server-controlled crafting is available for two game versions:

| Minecraft | NeoForge build tested | Java | Module | Runtime JAR |
|---|---|---|---|---|
| 1.21.1 | 21.1.264 | 21 | [1.21.1](1.21.1/README.md) | `crafttakestime-neoforge-1.21.1-2.0.0-beta.2.jar` |
| 26.3 | 26.3.0.95 | 25 | [26.3](26.3/README.md) | `crafttakestime-neoforge-26.3-2.0.0-beta.2.jar` |

Both builds support the inventory grid, crafting table, anvil, smithing table and stonecutter. Duration includes configurable ingredient, output and menu multipliers, experience bonuses and the `crafttakestime:crafting_speed` attribute. Normal clicks craft one recipe; Shift clicks repeat one timed recipe at a time.

Crafting settings use standard world datapacks through NeoForge Data Maps, with item IDs, item tags and `/reload` support. Per-stack data components can override item timing, and equipment can modify the crafting speed attribute. See [DATAPACKS.md](DATAPACKS.md) for the complete configuration reference.

Build and test from the chosen module directory:

```sh
cd 1.21.1  # or 26.3
./gradlew build
./gradlew runGameTestServer
./gradlew runClient
```

Install the matching JAR from the module's `build/libs` directory on both client and server. The two game versions use separate JARs. Development tests are excluded from these JARs.

See the module READMEs for controls and configuration, the module `VALIDATION.md` files for tested behavior and compatibility limits, and [CHANGELOG.md](CHANGELOG.md) for release notes. The existing CurseForge project is [Craft Takes Time](https://www.curseforge.com/minecraft/mc-mods/craft-time); source and issues are hosted here.

## Other versions

The existing `1.12`, `1.16`, `1.18`, `1.19`, `1.20`, `1.20-fabric` and `1.21` projects retain their version-specific build setups. The `1.21` directory is the earlier Forge project; `1.21.1` is the NeoForge 2.x module. General documentation for earlier versions is available in [docs/General.md](docs/General.md).
