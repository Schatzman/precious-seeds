package preciousseeds;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Central registry of precious crop lines. To add a fourth crop, append a new enum constant with a
 * unique basename, pick a {@link MapColor}, choose the mature {@link Items} drop, then add matching
 * assets under {@code assets/precious_seeds} and a loot table under
 * {@code data/precious_seeds/loot_tables/blocks/}. If the line should also drop from world blocks,
 * register another branch in {@link PreciousSeedBonusLoot}. See README section "Adding a fourth crop".
 */
public enum PreciousCropVariants {
	DIAMOND("diamond", Items.DIAMOND, MapColor.DIAMOND),
	EMERALD("emerald", Items.EMERALD, MapColor.EMERALD),
	DIRT("dirt", Items.DIRT, MapColor.DIRT);

	public static final PreciousCropVariants[] ALL = values();

	private final String basename;
	private final Item matureResourceItem;
	private final MapColor mapColor;
	private Block bushBlock;
	private Item seedsItem;

	PreciousCropVariants(String basename, Item matureResourceItem, MapColor mapColor) {
		this.basename = basename;
		this.matureResourceItem = matureResourceItem;
		this.mapColor = mapColor;
	}

	public String basename() {
		return basename;
	}

	public Item matureResourceItem() {
		return matureResourceItem;
	}

	public Block bushBlock() {
		return bushBlock;
	}

	public Item seedsItem() {
		return seedsItem;
	}

	public ResourceLocation bushId() {
		return new ResourceLocation(PreciousSeedsMod.MOD_ID, basename + "_bush");
	}

	public ResourceLocation seedsId() {
		return new ResourceLocation(PreciousSeedsMod.MOD_ID, basename + "_seeds");
	}

	static void registerBlocksAndItems() {
		for (PreciousCropVariants variant : ALL) {
			LateRef<Item> seedRef = new LateRef<>();
			BlockBehaviour.Properties props = BlockBehaviour.Properties.copy(Blocks.WHEAT).mapColor(variant.mapColor);
			Block bush = new PreciousCropBlock(props, seedRef::get);
			Registry.register(BuiltInRegistries.BLOCK, variant.bushId(), bush);
			Item seeds = new PreciousSeedsItem(bush, new Item.Properties());
			Registry.register(BuiltInRegistries.ITEM, variant.seedsId(), seeds);
			seedRef.value = seeds;
			variant.bushBlock = bush;
			variant.seedsItem = seeds;
		}
	}

	private static final class LateRef<T> {
		T value;

		T get() {
			return value;
		}
	}
}
