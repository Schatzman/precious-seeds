package preciousseeds;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

/**
 * Adds independent bonus pools for precious seeds when breaking certain blocks. Vanilla pools are
 * left untouched; these rolls are additional.
 */
public final class PreciousSeedBonusLoot {
	private PreciousSeedBonusLoot() {
	}

	public static void register() {
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (!source.isBuiltin()) {
				return;
			}
			if (id.equals(Blocks.DIAMOND_ORE.getLootTable())) {
				addChancePool(tableBuilder, PreciousCropVariants.DIAMOND.seedsItem(), 0.2f);
			} else if (id.equals(Blocks.EMERALD_ORE.getLootTable())) {
				addChancePool(tableBuilder, PreciousCropVariants.EMERALD.seedsItem(), 0.5f);
			} else if (id.equals(Blocks.DIRT.getLootTable())) {
				addChancePool(tableBuilder, PreciousCropVariants.DIRT.seedsItem(), 0.01f);
			}
		});
	}

	private static void addChancePool(LootTable.Builder tableBuilder, net.minecraft.world.item.Item item, float chance) {
		tableBuilder.withPool(
				LootPool.lootPool()
						.when(LootItemRandomChanceCondition.randomChance(chance))
						.add(LootItem.lootTableItem(item)));
	}
}
