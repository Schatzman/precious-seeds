# Art inventory — Precious Seeds

Vanilla **Minecraft 1.20.1** sources (from the Loom **merged client jar** under `.gradle/loom-cache/.../minecraft-merged-...-1.20.1-...jar`, path `assets/minecraft/`) and mod targets under `src/main/resources/assets/precious_seeds/`.

**Placeholders (2026-04-24):** All mod textures below were copied from vanilla so you can **edit files in place** (same path, same filename) in your image editor.

| Vanilla source | Mod target(s) | Status |
|----------------|---------------|--------|
| `textures/block/wheat_stage0.png` … `wheat_stage7.png` | `textures/block/diamond_bush_stage0.png` … `diamond_bush_stage7.png` | Placeholder = wheat (edit per line) |
| Same | `textures/block/emerald_bush_stage0.png` … `emerald_bush_stage7.png` | Placeholder = wheat |
| Same | `textures/block/dirt_bush_stage0.png` … `dirt_bush_stage7.png` | Placeholder = wheat |
| `textures/item/wheat_seeds.png` | `textures/item/diamond_seeds.png` | Placeholder = wheat_seeds |
| Same | `textures/item/emerald_seeds.png` | Placeholder = wheat_seeds |
| Same | `textures/item/dirt_seeds.png` | Placeholder = wheat_seeds |
| Same | `icon.png` (mod icon in `assets/precious_seeds/`) | Placeholder = wheat_seeds (replace with your logo) |

**Optional references** (not copied; use for color/palette when drawing): `textures/item/diamond.png`, `textures/item/emerald.png`, `textures/block/dirt.png` in the same jar.

Replace placeholders with original art when ready; keep **filenames** aligned with `models/` and `blockstates/`.

**Extract command** (for re-running after jar path changes): `unzip -j` from the `minecraft-merged-...-1.20.1-...jar` in `repos/precious-seeds/.gradle/loom-cache/`.
