package preciousseeds;

import java.util.HashSet;
import java.util.Set;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
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
 *
 * <p><strong>Extensibility</strong> diamond seed drops target every block in {@link
 * BlockTags#DIAMOND_ORES} and emerald in {@link BlockTags#EMERALD_ORES} (e.g. deepslate + any mod
 * ore the pack adds to those tags) plus a small optional id list (below) for ores that are not in
 * the tag.
 */
public final class PreciousSeedBonusLoot {
	private static final int BINOMIAL_EXTRA_ROUNDS = 1;

	/**
	 * Modded ores not always added to #minecraft:diamond_ores by their authors — add known deposit
	 * block ids; missing ids are ignored.
	 */
	private static final String[][] OPTIONAL_DIAMOND_ORE_BLOCK_IDS = {
		// Better Nether (namespace betternetter / betternether; common deposit names — optional)
		{"betternetter", "netherrack_diamond_ore"},
		{"betternether", "netherrack_diamond_ore"},
		// Better End — end-stone family deposits (optional)
		{"betterend", "diamond_ore_endstone"},
		{"betterend", "diamond_ore_end_stone"},
		{"betterend", "diamond_ore_endstone_ore"},
		{"betterend", "diamond_ore_end_stone_ore"},
		{"betterend", "diamond_ore_flavolite"},
	};

	/** Optional emerald ores in nether/end; ignored if not registered. */
	private static final String[][] OPTIONAL_EMERALD_ORE_BLOCK_IDS = {
		{"betternetter", "netherrack_emerald_ore"},
		{"betternether", "netherrack_emerald_ore"},
		{"betterend", "emerald_ore_endstone"},
		{"betterend", "emerald_ore_end_stone"},
	};

	private static volatile Set<ResourceLocation> DIAMOND_LOOT_IDS;
	private static volatile Set<ResourceLocation> EMERALD_LOOT_IDS;

	private PreciousSeedBonusLoot() {
	}

	public static void register() {
		LootTableEvents.MODIFY.register(
				(resourceManager, lootManager, id, tableBuilder, source) -> {
					if (!source.isBuiltin()) {
						return;
					}
					if (diamondLootIds().contains(id)) {
						addFortuneScaledSeedPool(
								tableBuilder, PreciousCropVariants.DIAMOND.seedsItem(), 0.2f);
					} else if (emeraldLootIds().contains(id)) {
						addFortuneScaledSeedPool(
								tableBuilder, PreciousCropVariants.EMERALD.seedsItem(), 0.5f);
					} else if (id.equals(Blocks.DIRT.getLootTable())) {
						addFortuneScaledSeedPool(
								tableBuilder, PreciousCropVariants.DIRT.seedsItem(), 0.01f);
					}
				});
	}

	private static Set<ResourceLocation> diamondLootIds() {
		if (DIAMOND_LOOT_IDS == null) {
			synchronized (PreciousSeedBonusLoot.class) {
				if (DIAMOND_LOOT_IDS == null) {
					DIAMOND_LOOT_IDS =
							Set.copyOf(
									lootFromTagWithFallbacks(
											BlockTags.DIAMOND_ORES,
											new Block[] {Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE},
											OPTIONAL_DIAMOND_ORE_BLOCK_IDS));
				}
			}
		}
		return DIAMOND_LOOT_IDS;
	}

	private static Set<ResourceLocation> emeraldLootIds() {
		if (EMERALD_LOOT_IDS == null) {
			synchronized (PreciousSeedBonusLoot.class) {
				if (EMERALD_LOOT_IDS == null) {
					EMERALD_LOOT_IDS =
							Set.copyOf(
									lootFromTagWithFallbacks(
											BlockTags.EMERALD_ORES,
											new Block[] {Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE},
											OPTIONAL_EMERALD_ORE_BLOCK_IDS));
				}
			}
		}
		return EMERALD_LOOT_IDS;
	}

	private static Set<ResourceLocation> lootFromTagWithFallbacks(
			net.minecraft.tags.TagKey<Block> tag, Block[] fallbacks, String[][] optionalIds) {
		Set<ResourceLocation> out = new HashSet<>();
		var tagOpt = BuiltInRegistries.BLOCK.getTag(tag);
		if (tagOpt.isPresent()) {
			for (Holder<Block> h : tagOpt.get()) {
				out.add(h.value().getLootTable());
			}
		}
		for (Block b : fallbacks) {
			out.add(b.getLootTable());
		}
		for (String[] nsPath : optionalIds) {
			addBlockLootIfPresent(out, nsPath[0], nsPath[1]);
		}
		return out;
	}

	private static void addBlockLootIfPresent(Set<ResourceLocation> out, String namespace, String path) {
		try {
			ResourceLocation rid = new ResourceLocation(namespace, path);
			if (BuiltInRegistries.BLOCK.containsKey(rid)) {
				out.add(BuiltInRegistries.BLOCK.get(rid).getLootTable());
			}
		} catch (@SuppressWarnings("unused") Exception ignored) {
			// invalid resource location
		}
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
