# Precious Seeds

Fabric mod for **Minecraft 1.20.1**. Grow **diamond**, **emerald**, and **dirt** from wheat-style crops (ages 0–7 on farmland). Mature harvests drop the resource and extra seeds for replanting.

## Requirements

- Minecraft **1.20.1**
- **Fabric Loader** ≥ **0.18.4** (see `fabric.mod.json`; dev pin in `gradle.properties`)
- **Fabric API**

## Mechanics

| Crop        | Seeds            | Bush            | Mature drops                          |
|-------------|------------------|-----------------|----------------------------------------|
| Diamond line | `diamond_seeds` | `diamond_bush` | **1–3** `minecraft:diamond` + **1–2** diamond seeds (uniform) |
| Emerald line | `emerald_seeds` | `emerald_bush` | **1–3** `minecraft:emerald` + **1–2** emerald seeds |
| Dirt line   | `dirt_seeds`     | `dirt_bush`     | **1–3** `minecraft:dirt` + **1–2** dirt seeds |

Immature breaks drop **1** seed (aligned with a simple wheat-like pattern).

### World bonus seeds (additional loot)

Separate loot pools are added with exact `random_chance` probabilities; **vanilla pools are not removed**.

| Block broken        | Extra item              | Chance   |
|---------------------|-------------------------|----------|
| `minecraft:diamond_ore` | `precious_seeds:diamond_seeds` | **20%** |
| `minecraft:emerald_ore` | `precious_seeds:emerald_seeds` | **50%** |
| `minecraft:dirt`        | `precious_seeds:dirt_seeds`     | **1%**  |

**Fortune:** For **diamond ore** and **emerald ore**, the **bonus seed roll is independent of Fortune** (it uses its own `random_chance` pool and does not use Fortune-based loot functions). Vanilla ore drops still follow normal Fortune rules; seeds are an extra roll on top.

## Build

```bash
./gradlew build
```

Output: `build/libs/precious-seeds-<version>.jar` — place in the `mods` folder.

## Run client (smoke)

```bash
./gradlew runClient
```

Use the **Precious Seeds** creative tab to grab seeds; plant on hydrated farmland; bone meal to test growth.

## Adding a fourth crop

1. Add a new constant to `PreciousCropVariants` (`basename`, mature `Items.*` drop, `MapColor`).
2. Add textures under `assets/precious_seeds/textures/` (eight bush stages + seed), models, and `blockstates/<basename>_bush.json`.
3. Add `data/precious_seeds/loot_tables/blocks/<basename>_bush.json` (mature resource + seed ranges; immature seed drop).
4. Optional world drops: extend `PreciousSeedBonusLoot` with another `LootTableEvents.MODIFY` branch and document the chance here.

See `docs/ART_INVENTORY.md` for texture provenance.

## License

See `LICENSE` (template default **CC0-1.0**; change before publishing if you prefer another license).
