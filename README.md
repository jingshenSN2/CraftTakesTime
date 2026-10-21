# Craft Takes Time

CraftTakesTime makes manual crafting take time. Each Minecraft version has its own project directory.

## NeoForge 26.3

The [26.3 module](26.3/README.md) targets Minecraft 26.3, NeoForge 26.3.0.95 and Java 25. This alpha implements server-controlled crafting jobs for the inventory grid, crafting table, anvil, smithing table and stonecutter, with configurable duration, experience bonuses and a crafting-speed attribute.

```sh
cd 26.3
./gradlew build
./gradlew runGameTestServer
./gradlew runClient
```

Install `26.3/build/libs/crafttakestime-2.0.0-alpha.1.jar` on both the client and server. See the [module documentation](26.3/README.md) for controls and configuration, and [validation notes](26.3/VALIDATION.md) for tested behavior and compatibility limits.

## Other versions

The existing `1.12`, `1.16`, `1.18`, `1.19`, `1.20`, `1.20-fabric` and `1.21` projects retain their version-specific build setups. General documentation for those versions is available in [docs/General.md](docs/General.md).
