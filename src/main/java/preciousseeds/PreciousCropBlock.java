package preciousseeds;

import java.util.function.Supplier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class PreciousCropBlock extends CropBlock {
	private final Supplier<ItemLike> baseSeed;

	public PreciousCropBlock(BlockBehaviour.Properties properties, Supplier<ItemLike> baseSeed) {
		super(properties);
		this.baseSeed = baseSeed;
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return baseSeed.get();
	}
}
