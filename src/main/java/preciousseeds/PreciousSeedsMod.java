package preciousseeds;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PreciousSeedsMod implements ModInitializer {
	public static final String MOD_ID = "precious_seeds";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PreciousCropVariants.registerBlocksAndItems();
		registerCreativeTab();
		PreciousSeedBonusLoot.register();
		LOGGER.info("Precious Seeds initialized ({} crop lines).", PreciousCropVariants.ALL.length);
	}

	private static void registerCreativeTab() {
		CreativeModeTab tab = FabricItemGroup.builder()
				.title(Component.translatable("itemGroup.precious_seeds.precious_seeds"))
				.icon(() -> new ItemStack(PreciousCropVariants.DIAMOND.seedsItem()))
				.displayItems((parameters, output) -> {
					for (PreciousCropVariants v : PreciousCropVariants.ALL) {
						output.accept(v.seedsItem());
					}
				})
				.build();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(MOD_ID, "precious_seeds"), tab);
	}
}
