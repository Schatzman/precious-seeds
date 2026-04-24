package preciousseeds;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

/**
 * Crops with transparent texture pixels must use the cutout render type; otherwise transparent areas
 * render as black. Wheat and other vanilla crops are registered the same way on the client.
 */
public final class PreciousSeedsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Block[] bushes = new Block[PreciousCropVariants.ALL.length];
		for (int i = 0; i < PreciousCropVariants.ALL.length; i++) {
			bushes[i] = PreciousCropVariants.ALL[i].bushBlock();
		}
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), bushes);
	}
}
