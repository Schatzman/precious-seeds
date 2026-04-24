# Precious Seeds

Fabric mod for **Minecraft 1.20.1**. This is an **intentional cheat / fun amplification** mod: it is **not** aiming at survival balance, villager economy, or ore rarity. The goal is **predictable loot behavior** (including **Fortune**) and silly power, not a fair progression pack.

Grow **diamond**, **emerald**, and **dirt** from wheat-style crops (ages 0–7 on farmland). Mature harvests drop the resource and extra seeds for replanting.

## Requirements

- Minecraft **1.20.1**
- **Fabric Loader** ≥ **0.18.4** (see `fabric.mod.json`; dev pin in `gradle.properties`)
- **Fabric API**

## Mechanics

| Crop        | Seeds            | Bush            | Mature drops (Fortune 0 baseline)      |
|-------------|------------------|-----------------|----------------------------------------|
| Diamond line | `diamond_seeds` | `diamond_bush` | **1–3** `minecraft:diamond` + **1–2** diamond seeds (uniform) |
| Emerald line | `emerald_seeds` | `emerald_bush` | **1–3** `minecraft:emerald` + **1–2** emerald seeds |
| Dirt line   | `dirt_seeds`     | `dirt_bush`     | **1–3** `minecraft:dirt` + **1–2** dirt seeds |

Immature breaks drop **1** seed (aligned with a simple wheat-like pattern). **Fortune** does not apply to that immature pool.

### Mature bushes and Fortune

For **age 7** breaks, both the **resource** and **seed** pools use `minecraft:apply_bonus` with **`minecraft:fortune`** and the **`minecraft:uniform_bonus_count`** formula (`bonusMultiplier` **1**), matching vanilla’s uniform Fortune bonus style. After the base `set_count` roll, the stack gains an extra **0–Fortune** (uniform), so **Fortune III** adds **0–3** to each of the resource count and the seed count independently.

### World bonus seeds (additional loot)

Separate loot pools are added; **vanilla pools are not removed**. Implementation: `LootTableEvents.MODIFY` in `PreciousSeedBonusLoot` builds each pool with `set_count` **0**, then **`minecraft:apply_bonus`** with **`minecraft:fortune`** and **`minecraft:binomial_with_bonus_count`** (`extra` **1**, `probability` per line). That matches the same **binomial-with-bonus** family vanilla uses for extra mature **wheat seeds** (see `data/minecraft/loot_tables/blocks/wheat.json`).

Let **F** be the tool’s **Fortune** level (0–3 in survival). Each break runs **F + 1** independent trials; each trial succeeds with the line’s probability and adds **one** seed. So the number of seeds is **Binomial(F + 1, p)** (zero seeds is allowed).

| Block broken           | Extra item                     | Probability **p** | Fortune 0 (1 trial) | Fortune III (4 trials) — expected seeds |
|------------------------|--------------------------------|-------------------|----------------------|-------------------------------------------|
| `minecraft:diamond_ore` | `precious_seeds:diamond_seeds` | **0.2** (20%)     | **20%** chance of 1 seed; else 0 | **~0.8** seeds on average (0–4 possible) |
| `minecraft:emerald_ore` | `precious_seeds:emerald_seeds` | **0.5** (50%)   | **50%** chance of 1 seed; else 0 | **~2** seeds on average (0–4 possible) |
| `minecraft:dirt`       | `precious_seeds:dirt_seeds`    | **0.01** (1%)     | **1%** chance of 1 seed; else 0 | **~0.04** seeds on average (0–4 possible) |

**Silk Touch (ores):** These bonus pools are **not** gated on Silk Touch vs Fortune ore outcomes. If the block breaks and loot runs (explosion decay applies), **bonus seeds can still roll** even when Silk Touch replaces gem drops—same general idea as an independent extra pool.

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
3. Add `data/precious_seeds/loot_tables/blocks/<basename>_bush.json` (mature resource + seed ranges with Fortune `apply_bonus` as above; immature seed drop).
4. Optional world drops: extend `PreciousSeedBonusLoot` with another branch and document **p** and Fortune behavior here.

See `docs/ART_INVENTORY.md` for texture provenance.

## License

Licensed under **CC0-1.0**; see [`LICENSE`](LICENSE).
