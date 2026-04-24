package preciousseeds;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/**
 * Adds independent bonus pools for precious seeds when breaking certain blocks. Vanilla pools are
 * left untouched; these rolls are additional.
 *
 * <p>Seed yield uses {@code minecraft:apply_bonus} with {@link Enchantments#BLOCK_FORTUNE} and the
 * {@code binomial_with_bonus_count} formula (same family as mature wheat seeds): count starts at 0,
 * then each block break runs {@code Fortune + extra} Bernoulli trials with the line's probability;
 * each success adds one seed. With {@code extra = 1}, Fortune 0 matches the design baselines (20% /
 * 50% / 1%); higher Fortune adds more trials so expected seeds increase.
 */
public final class PreciousSeedBonusLoot {
	private static final int BINOMIAL_EXTRA_ROUNDS = 1;

	private PreciousSeedBonusLoot() {
	}

	public static void register() {
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (!source.isBuiltin()) {
				return;
			}
			if (id.equals(Blocks.DIAMOND_ORE.getLootTable())) {
				addFortuneScaledSeedPool(tableBuilder, PreciousCropVariants.DIAMOND.seedsItem(), 0.2f);
			} else if (id.equals(Blocks.EMERALD_ORE.getLootTable())) {
				addFortuneScaledSeedPool(tableBuilder, PreciousCropVariants.EMERALD.seedsItem(), 0.5f);
			} else if (id.equals(Blocks.DIRT.getLootTable())) {
				addFortuneScaledSeedPool(tableBuilder, PreciousCropVariants.DIRT.seedsItem(), 0.01f);
			}
		});
	}

	private static void addFortuneScaledSeedPool(
			net.minecraft.world.level.storage.loot.LootTable.Builder tableBuilder,
			net.minecraft.world.item.Item item,
			float binomialProbability) {
		tableBuilder.withPool(
				LootPool.lootPool()
						.add(
								LootItem.lootTableItem(item)
										.apply(SetItemCountFunction.setCount(ConstantValue.exactly(0)))
										.apply(
												ApplyBonusCount.addBonusBinomialDistributionCount(
														Enchantments.BLOCK_FORTUNE,
														binomialProbability,
														BINOMIAL_EXTRA_ROUNDS))
										.apply(ApplyExplosionDecay.explosionDecay())));
	}
}
