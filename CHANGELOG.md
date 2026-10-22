# Changelog

## 2.0.0-beta.2 — NeoForge — 2026-10-22

Separate builds for Minecraft 1.21.1 and 26.3.

- Configure crafting through native world datapacks and NeoForge Data Maps, including item tags, menu types, namespace multipliers, global settings and sound.
- Add the persistent `crafttakestime:crafting_time` item component for per-stack ingredient and output timing.
- Apply changes with `/reload`; cancel existing tasks without consuming ingredients.
- Keep the player crafting speed attribute and native equipment modifier support.
- Include a version-specific example datapack and configuration reference.

The previous `config/crafttakestime/craft_time_config.json` is left untouched and is no longer read or generated. Transfer custom values into a datapack using [DATAPACKS.md](DATAPACKS.md).

Validation: 17 custom GameTests pass on each version, including native external-datapack reload, cancellation, timing and component persistence. Each version also passed a real client/integrated-server crafting test using a datapack-provided duration. Third-party menu compatibility and remote multiplayer remain outside the verified scope.

## 2.0.0-beta.1 — NeoForge — 2026-10-21

Available as separate builds for Minecraft 1.21.1 and 26.3. Install the build matching your game version on both the client and server.

- Move crafting duration and completion authorization to the server.
- Support inventory crafting, crafting tables, anvils, smithing tables and stonecutters.
- Keep global, menu, ingredient and output multipliers, plus experience-based speed bonuses.
- Add the native `crafttakestime:crafting_speed` attribute for commands and attribute modifiers.
- Show a server-synchronized progress bar at the result slot, with crafting and completion sounds.
- Make left/right click perform one complete recipe after waiting; Shift click repeats individually timed recipes into the inventory.
- Cancel when inputs or the menu change, or the output can no longer fit.
- Preserve vanilla crafting remainders, anvil costs and smithing consumption.
- Guard vanilla result-slot extraction paths against taking an untimed recipe.
- Provide an adapter API for custom crafting menus.

Validation: 15 custom dedicated-server GameTests pass on each game version. Each build also passed an actual client/integrated-server crafting test with progress synchronization and item consumption. Test code and test worlds are excluded from runtime JARs.

Compatibility: third-party crafting stations and JEI/InvMove/Curios combinations have not been tested. Remote multiplayer and high-latency behavior remain unverified. Review the module validation notes for scope. Legacy GUI/class-name configuration requires manual adaptation to the new menu-ID configuration.
